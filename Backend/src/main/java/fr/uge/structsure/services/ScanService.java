package fr.uge.structsure.services;

import fr.uge.structsure.dto.scan.AndroidScanResultDTO;
import fr.uge.structsure.entities.*;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.ResultRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ScanService {
    private static final Logger logger = LoggerFactory.getLogger(ScanService.class);
    private final ResultRepository resultRepository;

    @Autowired
    public ScanService(ResultRepository resultRepository) {
        this.resultRepository = Objects.requireNonNull(resultRepository);
    }


    @Transactional
    public void saveScanResults(AndroidScanResultDTO scanData) throws TraitementException {
        try {
            List<Result> results = new ArrayList<>();

            Scan scan = new Scan();
            scan.setId(scanData.scanId());

            for (var sensorResult : scanData.results()) {
                Sensor sensor = new Sensor();
                sensor.setSensorId(new SensorId(
                        sensorResult.control_chip(),
                        sensorResult.measure_chip()
                ));

                Result result = new Result(
                        State.valueOf(sensorResult.state()),
                        sensor,
                        scan
                );
                results.add(result);
            }

            resultRepository.saveAll(results);

            logger.info("Saved {} results for scan {}", results.size(), scanData.scanId());
        } catch (Exception e) {
            logger.error("Error saving results: {}", e.getMessage());
            throw new TraitementException(Error.SERVER_ERROR);
        }
    }
}