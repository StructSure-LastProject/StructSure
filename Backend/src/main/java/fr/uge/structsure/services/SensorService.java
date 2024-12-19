package fr.uge.structsure.services;

import fr.uge.structsure.dto.sensors.SensorDTO;
import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.repositories.SensorRepository;
import fr.uge.structsure.utils.sort.SortStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SensorService {
    private final SensorRepository sensorRepository;

    @Autowired
    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public List<SensorDTO> getSensorDTOsByStructure(
            Long structureId,
            String tri,
            String ordre,
            String filtreEtat,
            LocalTime dateInstallationMin,
            LocalTime dateInstallationMax) {

        var sensors = sensorRepository.findByStructureId(structureId);
        if (sensors.isEmpty()) {
            throw new RuntimeException("Ouvrage introuvable");
        }

        // Filtrage par état (archivé ou actif)
        sensors = sensors.stream()
                .filter(sensor -> {
                    if ("actif".equalsIgnoreCase(filtreEtat)) {
                        return Boolean.FALSE.equals(sensor.getArchived());
                    } else if ("archivé".equalsIgnoreCase(filtreEtat)) {
                        return Boolean.TRUE.equals(sensor.getArchived());
                    }
                    return true; // Aucun filtre
                })
                .collect(Collectors.toList());

        // Application du tri avec le design pattern Strategy
        var comparator = SortStrategyFactory.getStrategy(tri).getComparator();
        if ("desc".equalsIgnoreCase(ordre)) {
            comparator = comparator.reversed();
        }

        return sensors.stream()
                .sorted(comparator)
                .map(SensorDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
