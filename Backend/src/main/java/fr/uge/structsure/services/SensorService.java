package fr.uge.structsure.services;

import fr.uge.structsure.dto.sensors.AddSensorAnswerDTO;
import fr.uge.structsure.dto.sensors.AddSensorRequestDTO;
import fr.uge.structsure.dto.sensors.SensorDTO;
import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.PlanRepository;
import fr.uge.structsure.repositories.ResultRepository;
import fr.uge.structsure.repositories.SensorRepository;
import fr.uge.structsure.repositories.StructureRepository;
import fr.uge.structsure.utils.StateEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

@Service
public class SensorService {
    private final SensorRepository sensorRepository;
    private final StructureRepository structureRepository;
    private final ResultRepository resultRepository;
    private final PlanRepository planRepository;

    @Autowired
    public SensorService(SensorRepository sensorRepository, StructureRepository structureRepository, ResultRepository resultRepository, PlanRepository planRepository) {
        this.sensorRepository = sensorRepository;
        this.structureRepository = structureRepository;
        this.resultRepository = resultRepository;
        this.planRepository = planRepository;
    }


    /**
     * Return the list of sensors with state
     * @param structureId the structure id
     * @return List<SensorDTO> list of sensors
     * @throws TraitementException if there is no structure with the id
     */
    public List<SensorDTO> getSensorsByStructureId(long structureId) throws TraitementException {
        var structure = structureRepository.findById(structureId);
        if (structure.isEmpty()) {
            throw new TraitementException(Error.STRUCTURE_ID_NOT_FOUND);
        }
        var sensors = sensorRepository.findByStructureId(structureId);
        return sensors.stream().map(sensor -> SensorDTO.fromEntityAndState(sensor, getSensorState(sensor))).toList();
    }

    /**
     * Returns the list of sensors present in a plan
     * @param structureId the structure id
     * @param planId the plan id
     * @return List<SensorDTO> the list of the sensors
     */
    public List<SensorDTO> getSensorsByPlanId(long structureId, long planId) throws TraitementException {
        var structure = structureRepository.findById(structureId);
        if (structure.isEmpty()) {
            throw new TraitementException(Error.STRUCTURE_ID_NOT_FOUND);
        }
        var plan = planRepository.findByStructureAndPlanId(planId, structure.get());
        if (plan.isEmpty()) {
            throw new TraitementException(Error.PLAN_NOT_FOUND);
        }
        var sensors = sensorRepository.
    }

    /**
     * Returns the state of the sensor
     * @param sensor the sensor
     * @return StateEnum the state
     */
    private StateEnum getSensorState(Sensor sensor) {
        var numberOfResults = resultRepository.countBySensor(sensor);
        if (numberOfResults == 0) {
            return StateEnum.UNKNOWN;
        }
        var isNokPresent = resultRepository.existsResultWithNokState(sensor);
        if (isNokPresent) {
            return StateEnum.NOK;
        }
        var isDefecitvePresent = resultRepository.existsResultWithDefectiveState(sensor);
        if (isDefecitvePresent) {
            return StateEnum.DEFECTIVE;
        }
        return StateEnum.OK;
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
        sensorEmptyPrecondition(request);
        sensorMalformedPrecondition(request);
        if (request.measureChip().equals(request.controlChip())) {
            throw new TraitementException(Error.SENSOR_CHIP_TAGS_ARE_IDENTICAL);
        }
        var structure = structureRepository.findById(request.structureId());
        if (structure.isEmpty()) {
            throw new TraitementException(Error.SENSOR_STRUCTURE_NOT_FOUND);
        }
        var alreadyUsedName = sensorRepository.findByName(request.name());
        if (alreadyUsedName.isPresent()) {
            throw new TraitementException(Error.SENSOR_NAME_ALREADY_EXISTS);
        }
        var alreadyUsedSensorId = !sensorRepository.findByChipTag(request.controlChip()).isEmpty();
        if (alreadyUsedSensorId) {
            throw new TraitementException(Error.SENSOR_CHIP_TAGS_ALREADY_EXISTS);
        }
        var sensor = new Sensor(request.controlChip(),
                request.measureChip(),
                request.name(),
                request.note() == null ? "": request.note(),
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
    private void sensorEmptyPrecondition(AddSensorRequestDTO request) throws TraitementException {
        Objects.requireNonNull(request);
        if (request.controlChip() == null || request.measureChip() == null) {
            throw new TraitementException(Error.SENSOR_CHIP_TAGS_IS_EMPTY);
        }
        if (request.name() == null) {
            throw new TraitementException(Error.SENSOR_NAME_IS_EMPTY);
        }
        if (request.installationDate() == null || request.installationDate().isEmpty()) {
            throw new TraitementException(Error.SENSOR_INSTALLATION_DATE_IS_EMPTY);
        }
    }

    /**
     * Ensures that the sensor request object contains all necessary information.
     *
     * This method checks that none of the required properties are malformed.
     * If any essential field is malformed, an exception is thrown.
     *
     * @param request An object containing the sensor information.
     * @throws TraitementException If any required property is malformed.
     */
    private void sensorMalformedPrecondition(AddSensorRequestDTO request) throws TraitementException {
        if (request.controlChip().isEmpty() || request.controlChip().length() > 32) {
            throw new TraitementException(Error.SENSOR_CHIP_TAGS_EXCEED_LIMIT);
        }
        if (request.measureChip().isEmpty() || request.measureChip().length() > 32) {
            throw new TraitementException(Error.SENSOR_CHIP_TAGS_EXCEED_LIMIT);
        }
        if (request.name().isEmpty() || request.name().length() > 32) {
            throw new TraitementException(Error.SENSOR_NAME_EXCEED_LIMIT);
        }
        if (request.note() != null && request.note().length() > 1000) {
            throw new TraitementException(Error.SENSOR_NOTE_EXCEED_LIMIT);
        }
        var formatter = DateTimeFormatter.ofPattern("uuuu-MM-dd");
        try {
            LocalDate.parse(request.installationDate(), formatter);
        } catch (DateTimeParseException e) {
            throw new TraitementException(Error.SENSOR_INSTALLATION_DATE_INVALID_FORMAT);
        }
    }


}
