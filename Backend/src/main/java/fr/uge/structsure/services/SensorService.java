package fr.uge.structsure.services;

import fr.uge.structsure.dto.sensors.SensorDTO;
import fr.uge.structsure.dto.sensors.SensorFilterDTO;
import fr.uge.structsure.dto.sensors.SensorResponseDTO;
import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.entities.State;
import fr.uge.structsure.repositories.SensorRepository;
import fr.uge.structsure.utils.sort.SortStrategyFactory;
import org.springframework.stereotype.Service;

import java.awt.Point;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SensorService {
    private final SensorRepository sensorRepository;

    public SensorService(SensorRepository sensorRepository) {
        this.sensorRepository = sensorRepository;
    }

    public List<SensorResponseDTO> getSensorsByStructureId(Long structureId, SensorFilterDTO filter) {
        if (filter.getDateInstallationMin() != null && filter.getDateInstallationMax() != null) {
            if (filter.getDateInstallationMin().isAfter(filter.getDateInstallationMax())) {
                throw new IllegalArgumentException("La date limite ne peut pas être inférieure à la date supérieure");
            }
        }

        List<Sensor> sensors = sensorRepository.findByStructureId(structureId);

        // Apply filters
        if (filter.getFiltreEtat() != null) {
            sensors = sensors.stream()
                    .filter(sensor -> sensor.getArchived() == filter.getFiltreEtat().equalsIgnoreCase("archivé"))
                    .collect(Collectors.toList());
        }

        // Apply sorting
        Comparator<Sensor> comparator = Comparator.comparing(Sensor::getName);
        if ("etat".equalsIgnoreCase(filter.getTri())) {
            comparator = Comparator.comparing(Sensor::getArchived);
        } else if ("dateInstallation".equalsIgnoreCase(filter.getTri())) {
            comparator = Comparator.comparing(Sensor::getInstallationDate);
        }
        if ("desc".equalsIgnoreCase(filter.getOrdre())) {
            comparator = comparator.reversed();
        }
        sensors.sort(comparator);

        // Map results to SensorResponseDTO
        return sensors.stream().map(sensor -> {
            SensorResponseDTO response = new SensorResponseDTO();
            response.setId(sensor.getSensorId().hashCode());
            response.setPlanId(sensor.getStructure().getId());
            response.setControlChip(sensor.getSensorId().getControlChip());
            response.setMeasureChip(sensor.getSensorId().getMeasureChip());
            response.setName(sensor.getName());
            response.setInstallationDate(sensor.getInstallationDate());
            response.setNote(sensor.getNote());
            response.setState(sensor.getArchived() ? "archivé" : "actif");
            response.setPosition(new SensorResponseDTO.Position(sensor.getX(), sensor.getY()));
            response.setLastState("OK");
            return response;
        }).collect(Collectors.toList());
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

    public List<Sensor> getSensorsByStructure(Long structureId) {
        return sensorRepository.findByStructureIdAndArchivedFalse(structureId);
    }

    public Point getSensorLocation(Sensor sensor) {
        return new Point(sensor.getX().intValue(), sensor.getY().intValue());
    }

    /*
    public List<Sensor> getSensorsByPlan(Long planId) {
        return sensorRepository.findByPlanIdAndArchivedFalse(planId);
    }

    */
}