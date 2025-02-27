package fr.uge.structsure.services;

import fr.uge.structsure.dto.scan.AndroidScanResultDTO;
import fr.uge.structsure.dto.scan.AndroidSensorEditDTO;
import fr.uge.structsure.entities.*;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service class responsible for handling scan-related operations.
 * This service manages the creation, validation, and storage of scan results.
 */
@Service
public class ScanService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScanService.class);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final ResultRepository resultRepository;
    private final ScanRepository scanRepository;
    private final StructureRepository structureRepository;
    private final AccountRepository accountRepository;
    private final SensorRepository sensorRepository;
    private final PlanRepository planRepository;

    /**
     * Constructs a new ScanService with the necessary repositories.
     *
     * @param scanRepository      Repository for Scan entities
     * @param resultRepository    Repository for Result entities
     * @param structureRepository Repository for Structure entities
     * @param accountRepository   Repository for Account entities
     * @param sensorRepository    Repository for Sensor entities
     * @param planRepository      Repository for Plan entities
     */
    @Autowired
    public ScanService(
            ScanRepository scanRepository,
            ResultRepository resultRepository,
            StructureRepository structureRepository,
            AccountRepository accountRepository,
            SensorRepository sensorRepository,
            PlanRepository planRepository
    ) {
        this.resultRepository = Objects.requireNonNull(resultRepository);
        this.scanRepository = Objects.requireNonNull(scanRepository);
        this.structureRepository = Objects.requireNonNull(structureRepository);
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.sensorRepository = Objects.requireNonNull(sensorRepository);
        this.planRepository = Objects.requireNonNull(planRepository);
    }

    /**
     * Saves the scan results received from an Android device.
     * This method validates the scan data, creates a new Scan entity,
     * processes the results, and saves them to the database.
     *
     * @param scanData The DTO containing the scan results from the Android device
     * @throws TraitementException If there's an error during the processing of the scan data
     */
    @Transactional
    public void saveScanResults(AndroidScanResultDTO scanData) throws TraitementException {
        validateScanData(scanData);
        if (scanAlreadyExists(scanData)) return;

        Structure structure = findStructure(scanData.structureId());
        Account account = findAccount(scanData.login());

        Scan scan = createScan(structure, scanData, account);
        processEdits(scanData.sensorEdits());
        List<Result> results = processResults(scan, scanData);

        resultRepository.saveAll(results);
        LOGGER.info(
            "Saved {} results and {} edits for scan {}",
            results.size(),
            scanData.sensorEdits().size(),
            scanData.scanId()
        );
    }

    /**
     * Validates the received scan data.
     *
     * @param scanData The scan data to validate
     * @throws TraitementException If the scan data is invalid (e.g., empty results)
     */
    private void validateScanData(AndroidScanResultDTO scanData) throws TraitementException {
        if (scanData.results().isEmpty() && scanData.sensorEdits().isEmpty()) {
            LOGGER.warn("Received empty scan results, ignoring");
            throw new TraitementException(Error.INVALID_FIELDS);
        }
    }

    /**
     * Tests if the scan given by the client has already been saved in
     * the database or not.
     * This case can occur when the data has been completely received
     * and computed, but the client has not received the response due
     * to lag, crash or connection loses.
     * @param scanData the data of the scan to search
     * @return true if already saved, false otherwise
     * @throws TraitementException if the scan time is not an ISO date
     */
    private boolean scanAlreadyExists(AndroidScanResultDTO scanData) throws TraitementException {
        LocalDateTime date = parseDate(scanData.launchDate());
        return scanRepository.findById(scanData.structureId()).stream()
            .filter(scan -> scan.getAuthor().getLogin().equals(scanData.login()))
            .anyMatch(scan -> scan.getDate().isEqual(date));
    }

    /**
     * Finds a Structure entity by its ID.
     *
     * @param structureId The ID of the structure to find
     * @return The found Structure entity
     * @throws TraitementException If the structure is not found
     */
    private Structure findStructure(Long structureId) throws TraitementException {
        return structureRepository.findById(structureId)
                .orElseThrow(() -> new TraitementException(Error.STRUCTURE_ID_NOT_FOUND));
    }

    /**
     * Finds an Account entity by the login.
     *
     * @param login The login of the account to find
     * @return The found Account entity
     * @throws TraitementException If the account is not found
     */
    private Account findAccount(String login) throws TraitementException {
        return accountRepository.findByLogin(login)
                .orElseThrow(() -> new TraitementException(Error.USER_ACCOUNT_NOT_FOUND));
    }

    /**
     * Creates a new Scan entity from the provided data.
     *
     * @param structure The Structure entity associated with the scan
     * @param scanData  The DTO containing the scan data
     * @param account   The Account entity associated with the scan
     * @return The created and saved Scan entity
     * @throws TraitementException if the scan time is not an ISO date
     */
    private Scan createScan(Structure structure, AndroidScanResultDTO scanData, Account account) throws TraitementException {
        LocalDateTime date = parseDate(scanData.launchDate());
        Scan scan = new Scan(structure, date, scanData.note(), account);
        return scanRepository.save(scan);
    }

    /**
     * Processes the results from the scan data and creates Result entities.
     *
     * @param scan     The Scan entity associated with these results
     * @param scanData The DTO containing the scan data and results
     * @return A list of created Result entities
     * @throws TraitementException If there's an error processing the results
     */
    private List<Result> processResults(Scan scan, AndroidScanResultDTO scanData) throws TraitementException {
        List<Result> results = new ArrayList<>();

        for (var rawResult : scanData.results()) {
            var sensorId = SensorId.from(rawResult.sensorId());
            var sensor = sensorRepository.findBySensorId(sensorId)
                .orElseThrow(() -> new TraitementException(Error.SENSOR_NOT_FOUND));
            Result result = new Result(State.valueOf(rawResult.state()), sensor, scan);
            results.add(result);
        }

        return results;
    }

    /**
     * Updates sensors or creates new ones based on the edits from the scan.
     *
     * @param edits All the editions done on sensors during the scan
     * @throws TraitementException if there's an error during processing
     */
    private void processEdits(List<AndroidSensorEditDTO> edits) throws TraitementException {
        var sensors = new ArrayList<Sensor>();

        for (var edit : edits) {
            var sensorId = SensorId.from(edit.sensorId());
            var existingSensor = sensorRepository.findBySensorId(sensorId);
            var structure = findStructure(edit.structureId());
            if (existingSensor.isPresent()) {
                Sensor sensor = existingSensor.get();
                if (edit.note() != null) sensor.setNote(edit.note());
                sensors.add(sensor);
            }
            else if (edit.controlChip() != null && edit.measureChip() != null) {
                if (edit.name() == null || edit.name().isBlank()) {
                    throw new TraitementException(Error.SENSOR_NAME_IS_EMPTY);
                }

                if (sensorRepository.nameAlreadyExists(edit.name())) {
                    throw new TraitementException(Error.SENSOR_NAME_ALREADY_EXISTS);
                }

                if (sensorRepository.chipTagAlreadyExists(edit.controlChip())) {
                    throw new TraitementException(Error.SENSOR_CHIP_TAGS_ALREADY_EXISTS);
                }

                Sensor newSensor = new Sensor(
                        edit.controlChip(),
                        edit.measureChip(),
                        edit.name(),
                        edit.note() != null ? edit.note() : "",
                        structure
                );

                if (edit.plan() != null) {
                    setPlan(edit, newSensor);
                }
                sensors.add(newSensor);
            }
        }

        if (!sensors.isEmpty()) {
            sensorRepository.saveAll(sensors);
        }
    }

    /**
     * Updates the given sensor with the plan and position found in
     * the given edit. The plan from the edit is assumed non-null.
     * @param edit the Android data containing new plan values
     * @param sensor the sensor to put values in
     */
    private void setPlan(AndroidSensorEditDTO edit, Sensor sensor) {
        if (edit.x() == null || edit.y() == null) {
            LOGGER.warn("Plan without coordinates received by Android will be ignored");
            return;
        }
        planRepository.findById(edit.plan()).ifPresentOrElse(
            plan -> {
                sensor.setPlan(plan);
                sensor.setX(edit.x());
                sensor.setY(edit.y());
            },
            () -> LOGGER.warn("Unknown plan with ID '{}' received from Android will be ignored", edit.plan())
        );
    }

    /**
     * Parses the given string into a date using {@link #DATETIME_FORMATTER}
     * @param date the date to parse
     * @return the corresponding LocalDateTime object
     * @throws TraitementException of the date format is not valid
     */
    private static LocalDateTime parseDate(String date) throws TraitementException {
        try {
            return LocalDateTime.parse(date, DATETIME_FORMATTER);
        } catch(DateTimeParseException e) {
            throw new TraitementException(Error.DATE_TIME_FORMAT_ERROR);
        }
    }
}