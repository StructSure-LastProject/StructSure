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

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class ScanService {
    private static final Logger logger = LoggerFactory.getLogger(ScanService.class);

    private final ScanRepository scanRepository;
    private final ResultRepository resultRepository;
    private final SensorRepository sensorRepository;

    @Autowired
    public ScanService(
            ScanRepository scanRepository,
            ResultRepository resultRepository,
            SensorRepository sensorRepository) {
        this.scanRepository = Objects.requireNonNull(scanRepository);
        this.resultRepository = Objects.requireNonNull(resultRepository);
        this.sensorRepository = Objects.requireNonNull(sensorRepository);
    }

    @Transactional(readOnly = true)
    public AndroidScanResultDTO getScanDetails(long scanId) throws TraitementException {
        logger.debug("Fetching scan details for ID: {}", scanId);

        Scan scan = findScanById(scanId);
        List<AndroidSensorResultDTO> sensorResults = convertResultsToDto(scan);

        return new AndroidScanResultDTO(
                scan.getId(),
                scan.getDate(),
                scan.getNote(),
                sensorResults
        );
    }

    @Transactional
    public void saveScanResults(AndroidScanResultDTO scanData) throws TraitementException {
        logger.debug("Saving scan results for scan ID: {}", scanData.scanId());

        Objects.requireNonNull(scanData, "Scan data cannot be null");
        Scan scan = findScanById(scanData.scanId());

        try {
            for (AndroidSensorResultDTO resultData : scanData.results()) {
                saveResult(scan, resultData);
            }
            logger.info("Successfully saved results for scan ID: {}", scanData.scanId());
        } catch (Exception e) {
            logger.error("Error saving scan results: {}", e.getMessage(), e);
            throw new TraitementException(Error.SERVER_ERROR);
        }
    }

    private void saveResult(Scan scan, AndroidSensorResultDTO resultData) throws TraitementException {
        Sensor sensor = findSensor(resultData.control_chip(), resultData.measure_chip());

        Result result = new Result(
                State.valueOf(resultData.state()),
                sensor,
                scan
        );

        resultRepository.save(result);
    }

    private Scan findScanById(long scanId) throws TraitementException {
        return scanRepository.findById(scanId)
                .orElseThrow(() -> {
                    logger.error("Scan not found with ID: {}", scanId);
                    return new TraitementException(Error.SCAN_NOT_FOUND);
                });
    }

    private List<AndroidSensorResultDTO> convertResultsToDto(Scan scan) throws TraitementException {
        try {
            return resultRepository.findByScan(scan)
                    .stream()
                    .map(this::convertResultToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error converting results to DTOs: {}", e.getMessage(), e);
            throw new TraitementException(Error.SERVER_ERROR);
        }
    }

    private AndroidSensorResultDTO convertResultToDto(Result result) {
        validateResult(result);
        Sensor sensor = result.getSensor();

        return new AndroidSensorResultDTO(
                sensor.getSensorId().toString(),
                sensor.getSensorId().getControlChip(),
                sensor.getSensorId().getMeasureChip(),
                sensor.getName(),
                result.getState().name(),
                sensor.getNote(),
                result.getScan().getDate()
        );
    }

    private void validateResult(Result result) {
        if (result.getSensor() == null) {
            logger.error("Sensor is null for result ID: {}", result.getId());
            throw new IllegalStateException("Sensor cannot be null");
        }

        Sensor sensor = result.getSensor();
        if (sensor.getSensorId() == null) {
            logger.error("SensorId is null for sensor: {}", sensor.getName());
            throw new IllegalStateException("SensorId cannot be null");
        }
    }

    private Sensor findSensor(String controlChip, String measureChip) throws TraitementException {
        return sensorRepository.findByChips(controlChip, measureChip)
            .orElseThrow(() -> {
                logger.error("Sensor not found with chips: {} and {}", controlChip, measureChip);
                return new TraitementException(Error.SENSOR_CHIP_TAGS_IS_EMPTY);
            });
    }
}