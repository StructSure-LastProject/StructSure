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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ScanService {
    private static final Logger logger = LoggerFactory.getLogger(ScanService.class);

    private final ScanRepository scanRepository;
    private final ResultRepository resultRepository;

    @Autowired
    public ScanService(ScanRepository scanRepository, ResultRepository resultRepository) {
        this.scanRepository = Objects.requireNonNull(scanRepository);
        this.resultRepository = Objects.requireNonNull(resultRepository);
    }

    @Transactional(readOnly = true)
    public AndroidScanResultDTO getScanDetails(long scanId) throws TraitementException {
        var scan = scanRepository.findById(scanId).orElseThrow(() -> new TraitementException(Error.SCAN_NOT_FOUND));

        List<AndroidSensorResultDTO> sensorResults = convertResultsToDto(scan);

        return new AndroidScanResultDTO(
                scan.getId(),
                scan.getDate(),
                scan.getNote(),
                sensorResults
        );
    }

    private List<AndroidSensorResultDTO> convertResultsToDto(Scan scan) throws TraitementException {
        try {
            return resultRepository.findByScan(scan)
                    .stream()
                    .map(this::convertResultToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error converting results to DTOs for scan {}: {}", scan.getId(), e.getMessage());
            throw new TraitementException(Error.SERVER_ERROR);
        }
    }

    private AndroidSensorResultDTO convertResultToDto(Result result) {
        if (result.getSensor() == null) {
            logger.error("Sensor is null for result ID: {}", result.getId());
            throw new IllegalStateException("Sensor cannot be null");
        }

        Sensor sensor = result.getSensor();
        if (sensor.getSensorId() == null) {
            logger.error("SensorId is null for sensor: {}", sensor.getName());
            throw new IllegalStateException("SensorId cannot be null");
        }

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
}