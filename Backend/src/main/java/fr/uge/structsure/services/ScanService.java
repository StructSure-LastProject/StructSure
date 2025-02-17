package fr.uge.structsure.services;

import fr.uge.structsure.dto.scan.AndroidScanResultDTO;
import fr.uge.structsure.entities.*;
import fr.uge.structsure.exceptions.ErrorIdentifier;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ScanService {
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

    /**
     * Trouve un capteur en utilisant ses chips de contrôle et de mesure
     */
    private Sensor findSensorByChips(String controlChip, String measureChip) throws TraitementException {
        List<Sensor> sensors = sensorRepository.findByChipTag(controlChip);

        // Trouve le capteur qui a les deux chips
        return sensors.stream()
                .filter(sensor ->
                        sensor.getSensorId().getControlChip().equals(controlChip) &&
                                sensor.getSensorId().getMeasureChip().equals(measureChip))
                .findFirst()
                .orElseThrow(() -> new TraitementException(ErrorIdentifier.SENSOR_CHIP_TAGS_IS_EMPTY));
    }

    /**
     * Traite les données de scan reçues depuis l'application Android
     */
    @Transactional
    public void processScanFromAndroid(AndroidScanResultDTO scanData) throws TraitementException {
        Objects.requireNonNull(scanData, "Scan data cannot be null");

        // Récupération du scan
        Scan scan = scanRepository.findById(scanData.scanId())
                .orElseThrow(() -> new TraitementException(ErrorIdentifier.SCAN_NOT_FOUND));

        // Traitement de chaque résultat
        for (var resultData : scanData.results()) {
            // Récupération du capteur par ses chips
            Sensor sensor = findSensorByChips(
                    resultData.control_chip(),
                    resultData.measure_chip()
            );

            // Création du résultat
            Result result = new Result(
                    State.valueOf(resultData.state()),
                    sensor,
                    scan
            );

            resultRepository.save(result);
        }
    }
}