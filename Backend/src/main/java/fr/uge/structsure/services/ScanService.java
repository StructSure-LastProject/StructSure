package fr.uge.structsure.services;

import fr.uge.structsure.dto.scan.AndroidScanResultDTO;
import fr.uge.structsure.dto.scan.AndroidSensorResultDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Service class responsible for handling scan-related operations.
 * This service manages the creation, validation, and storage of scan results.
 */
@Service
public class ScanService {
    private static final Logger logger = LoggerFactory.getLogger(ScanService.class);
    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    private final ResultRepository resultRepository;
    private final ScanRepository scanRepository;
    private final StructureRepository structureRepository;
    private final AccountRepository accountRepository;
    private final SensorRepository sensorRepository;

    /**
     * Constructs a new ScanService with the necessary repositories.
     *
     * @param scanRepository      Repository for Scan entities
     * @param resultRepository    Repository for Result entities
     * @param structureRepository Repository for Structure entities
     * @param accountRepository   Repository for Account entities
     * @param sensorRepository    Repository for Sensor entities
     */
    @Autowired
    public ScanService(
            ScanRepository scanRepository,
            ResultRepository resultRepository,
            StructureRepository structureRepository,
            AccountRepository accountRepository,
            SensorRepository sensorRepository
    ) {
        this.resultRepository = Objects.requireNonNull(resultRepository);
        this.scanRepository = Objects.requireNonNull(scanRepository);
        this.structureRepository = Objects.requireNonNull(structureRepository);
        this.accountRepository = Objects.requireNonNull(accountRepository);
        this.sensorRepository = Objects.requireNonNull(sensorRepository);
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

        Structure structure = findStructure(scanData.structureId());
        Account account = findAccount(scanData.login());

        Scan scan = createScan(structure, scanData, account);
        List<Result> results = processResults(scan, scanData);

        resultRepository.saveAll(results);
        logger.info("Saved {} results for scan {}", results.size(), scanData.scanId());
    }

    /**
     * Validates the received scan data.
     *
     * @param scanData The scan data to validate
     * @throws TraitementException If the scan data is invalid (e.g., empty results)
     */
    private void validateScanData(AndroidScanResultDTO scanData) throws TraitementException {
        if (scanData.results().isEmpty()) {
            logger.warn("Received empty scan results, ignoring");
            throw new TraitementException(Error.SERVER_ERROR);
        }
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
     */
    private Scan createScan(Structure structure, AndroidScanResultDTO scanData, Account account) {
        LocalDateTime launchDate = LocalDateTime.parse(scanData.launchDate(), DATETIME_FORMATTER);
        Scan scan = new Scan(structure, launchDate, scanData.note(), account);
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

        for (var sensorResult : scanData.results()) {
            Sensor sensor = findOrCreateSensor(sensorResult);
            updateSensorNoteIfChanged(sensor, sensorResult);

            Result result = createScanResult(sensor, scan, sensorResult);
            results.add(result);
        }

        return results;
    }

    /**
     * Finds an existing Sensor entity or creates a new one if not found.
     *
     * @param sensorResult The DTO containing the sensor data
     * @return The found or created Sensor entity
     * @throws TraitementException If the sensor is not found and cannot be created
     */
    private Sensor findOrCreateSensor(AndroidSensorResultDTO sensorResult) throws TraitementException {
        return sensorRepository.findBySensorId(
                new SensorId(sensorResult.control_chip(), sensorResult.measure_chip())
        ).orElseThrow(() -> new TraitementException(Error.SENSOR_NOT_FOUND));
    }

    /**
     * Updates the note of a Sensor entity if it has changed.
     *
     * @param sensor       The Sensor entity to update
     * @param sensorResult The DTO containing the new sensor data
     */
    private void updateSensorNoteIfChanged(Sensor sensor, AndroidSensorResultDTO sensorResult) {
        if (sensorResult.note() != null && !sensorResult.note().equals(sensor.getNote())) {
            sensor.setNote(sensorResult.note());
            sensorRepository.save(sensor);
            logger.info("Updated sensor note: {}", sensorResult.note());
        }
    }

    /**
     * Creates a new Result entity from the provided data.
     *
     * @param sensor       The Sensor entity associated with this result
     * @param scan         The Scan entity associated with this result
     * @param sensorResult The DTO containing the result data
     * @return The created Result entity
     */
    private Result createScanResult(Sensor sensor, Scan scan, AndroidSensorResultDTO sensorResult) {
        return new Result(
                State.valueOf(sensorResult.state()),
                sensor,
                scan
        );
    }
}