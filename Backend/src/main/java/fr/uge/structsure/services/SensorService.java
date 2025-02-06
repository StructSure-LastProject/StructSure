package fr.uge.structsure.services;

import fr.uge.structsure.dto.sensors.AddSensorAnswerDTO;
import fr.uge.structsure.dto.sensors.AddSensorRequestDTO;
import fr.uge.structsure.dto.sensors.SensorDTO;
import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.entities.SensorId;
import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.exceptions.ErrorIdentifier;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.SensorRepository;
import fr.uge.structsure.repositories.StructureRepository;
import fr.uge.structsure.utils.sort.SortStrategyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class SensorService {
    private final SensorRepository sensorRepository;
    private final StructureRepository structureRepository;

    @Autowired
    public SensorService(SensorRepository sensorRepository, StructureRepository structureRepository) {
        this.sensorRepository = sensorRepository;
        this.structureRepository = structureRepository;
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

    /**
     * Creates a sensor by validating preconditions and checking uniqueness constraints.
     *
     * This method first verifies that all required fields are present in the request.
     * Then, it ensures that the sensor's name and ID are unique within the system.
     * If the provided structure ID does not exist, an exception is thrown.
     * Finally, the sensor is saved in the repository and its identifier is returned.
     *
     * @param request An object containing the necessary information to create a sensor.
     * @return A DTO containing the identifiers of the created sensor.
     * @throws TraitementException If preconditions are not met or uniqueness constraints fail.
     */
    public AddSensorAnswerDTO createSensor(AddSensorRequestDTO request) throws TraitementException {
        sensorPrecondition(request);
        var structure = structureRepository.findById(request.id());
        if (structure.isEmpty()) {
            throw new TraitementException(ErrorIdentifier.SENSOR_STRUCTURE_NOT_FOUND);
        }
        var alreadyUsedName = sensorRepository.findByName(request.name());
        if (alreadyUsedName.isPresent()) {
            throw new TraitementException(ErrorIdentifier.SENSOR_NAME_ALREADY_EXISTS);
        }
        var alreadyUsedSensorId = sensorRepository.findBySensorId(new SensorId(request.controlChip(), request.measureChip()));
        if (alreadyUsedSensorId.isPresent()) {
            throw new TraitementException(ErrorIdentifier.SENSOR_ID_ALREADY_EXISTS);
        }
        var sensor = new Sensor(request.controlChip(),
                request.measureChip(),
                request.name(),
                request.note(),
                request.installationDate(),
                request.x(),
                request.y(),
                false,
                structure.get());
        var saved = sensorRepository.save(sensor);
        return new AddSensorAnswerDTO(saved.getSensorId().getControlChip(), saved.getSensorId().getMeasureChip());
    }

    /**
     * Ensures that the sensor request object contains all necessary information.
     *
     * This method checks that none of the required properties are null.
     * If any essential field is missing, an exception is thrown.
     *
     * @param request An object containing the sensor information.
     * @throws TraitementException If any required property is null.
     */
    private void sensorPrecondition(AddSensorRequestDTO request) throws TraitementException {
        Objects.requireNonNull(request);
        if (request.controlChip() == null) {
            throw new TraitementException(ErrorIdentifier.SENSOR_PROPERTIES_NOT_COMPLETE);
        }
        if (request.measureChip() == null) {
            throw new TraitementException(ErrorIdentifier.SENSOR_PROPERTIES_NOT_COMPLETE);
        }
        if (request.name() == null) {
            throw new TraitementException(ErrorIdentifier.SENSOR_PROPERTIES_NOT_COMPLETE);
        }
        if (request.note() == null) {
            throw new TraitementException(ErrorIdentifier.SENSOR_PROPERTIES_NOT_COMPLETE);
        }
        if (request.installationDate() == null) {
            throw new TraitementException(ErrorIdentifier.SENSOR_PROPERTIES_NOT_COMPLETE);
        }
    }
}
