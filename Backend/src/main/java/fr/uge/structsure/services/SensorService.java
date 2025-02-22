package fr.uge.structsure.services;

import fr.uge.structsure.dto.sensors.*;
import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.entities.State;
import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.PlanRepository;
import fr.uge.structsure.repositories.ResultRepository;
import fr.uge.structsure.repositories.SensorRepository;
import fr.uge.structsure.repositories.SensorRepositoryCriteriaQuery;
import fr.uge.structsure.repositories.StructureRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Objects;

/**
 * This class regroups all the services available for the sensor
 */
@Service
public class SensorService {
    private final SensorRepository sensorRepository;
    private final StructureRepository structureRepository;
    private final ResultRepository resultRepository;
    private final PlanRepository planRepository;
    private final SensorRepositoryCriteriaQuery sensorRepositoryCriteriaQuery;

    /**
     * Initialise the sensor service
     * @param sensorRepository the sensor repository
     * @param structureRepository the structure repository
     * @param resultRepository the result repository
     * @param planRepository the plan repository
     * @param sensorRepositoryCriteriaQuery the sensor repository using criteria query api
     */
    @Autowired
    public SensorService(SensorRepository sensorRepository, StructureRepository structureRepository, ResultRepository resultRepository, PlanRepository planRepository,
        SensorRepositoryCriteriaQuery sensorRepositoryCriteriaQuery) {
        this.sensorRepository = sensorRepository;
        this.structureRepository = structureRepository;
        this.resultRepository = resultRepository;
        this.planRepository = planRepository;
        this.sensorRepositoryCriteriaQuery = sensorRepositoryCriteriaQuery;
    }


    /**
     * Return the list of sensors with state
     * @param structureId the structure id
     * @return List<SensorDTO> list of sensors
     * @throws TraitementException if there is no structure with the id
     */
    public List<SensorDTO> getSensors(long structureId, AllSensorsByStructureRequestDTO request) throws TraitementException {
        request.checkFields();
        var structure = structureRepository.findById(structureId);
        if (structure.isEmpty()) {
            throw new TraitementException(Error.STRUCTURE_ID_NOT_FOUND);
        }
        return sensorRepositoryCriteriaQuery.findAllSensorsByStructureId(structureId, request);
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
        var plan = planRepository.findByStructureAndId(structure.get(), planId);
        if (plan.isEmpty()) {
            throw new TraitementException(Error.PLAN_NOT_FOUND);
        }
        var sensors = sensorRepository.findByPlan(plan.get());
        return sensors.stream().map(sensor -> SensorDTO.fromEntityAndState(sensor, getSensorState(sensor))).toList();
    }

    /**
     * Returns the state of the sensor
     * @param sensor the sensor
     * @return StateEnum the state
     */
    private State getSensorState(Sensor sensor) {
        var numberOfResults = resultRepository.countBySensor(sensor);
        if (numberOfResults == 0) {
            return State.UNKNOWN;
        }
        var isNokPresent = resultRepository.existsResultWithNokState(sensor);
        if (isNokPresent) {
            return State.NOK;
        }
        var isDefecitvePresent = resultRepository.existsResultWithDefectiveState(sensor);
        if (isDefecitvePresent) {
            return State.DEFECTIVE;
        }
        return State.OK;
    }

    /**
     * Creates a sensor by validating preconditions and checking uniqueness constraints.
     * <br>
     * This method first verifies that all required fields are present in the request.
     * Then, it ensures that the sensor's name and ID are unique within the system.
     * If the provided structure ID does not exist, an exception is thrown.
     * Finally, the sensor is saved in the repository and its identifier is returned.
     *
     * @param request An object containing the necessary information to create a sensor.
     * @return A DTO containing the identifiers of the created sensor.
     * @throws TraitementException If preconditions are not met or uniqueness constraints fail.
     */
    public AddSensorResponseDTO createSensor(BaseSensorDTO request) throws TraitementException {
        Objects.requireNonNull(request);
        addPlanAsserts(request);
        if (request.measureChip().equals(request.controlChip())) {
            throw new TraitementException(Error.SENSOR_CHIP_TAGS_ARE_IDENTICAL);
        }
        var structure = structureRepository.findById(request.structureId()).orElseThrow(() -> new TraitementException(Error.SENSOR_STRUCTURE_NOT_FOUND));
        checkState(structure);
        if (sensorRepository.chipTagAlreadyExists(request.controlChip())) {
            throw new TraitementException(Error.SENSOR_CHIP_TAGS_ALREADY_EXISTS);
        }
        if (sensorRepository.nameAlreadyExists(request.name())) {
            throw new TraitementException(Error.SENSOR_NAME_ALREADY_EXISTS);
        }
        var sensor = new Sensor(request.controlChip(),
                request.measureChip(),
                request.name(),
                request.note() == null ? "": request.note(),
                structure);
        var saved = sensorRepository.save(sensor);
        return new AddSensorResponseDTO(saved.getSensorId().getControlChip(), saved.getSensorId().getMeasureChip());
    }

    /**
     * Performs all the checks on the arguments of the function (add sensor)
     *
     * @param request An object containing the sensor information.
     * @throws TraitementException if validation fails
     */
    private void addPlanAsserts(BaseSensorDTO request) throws TraitementException {
        sensorEmptyPrecondition(request);
        sensorMalformedPrecondition(request);
    }

    /**
     * Checks if a structure is in an archived state.
     *
     * @param structure The structure to check
     * @throws TraitementException if the structure is archived
     */
    private void checkState(Structure structure) throws TraitementException {
        if (Boolean.TRUE.equals(structure.getArchived())) {
            throw new TraitementException(Error.PLAN_IS_ARCHIVED);
        }
    }

    /**
     * Ensures that the sensor request object contains all necessary information.
     * <br>
     * This method checks that none of the required properties are null.
     * If any essential field is missing, an exception is thrown.
     *
     * @param request An object containing the sensor information.
     * @throws TraitementException If any required property is null.
     */
    private void sensorEmptyPrecondition(BaseSensorDTO request) throws TraitementException {
        Objects.requireNonNull(request);
        if (request.controlChip() == null || request.measureChip() == null) {
            throw new TraitementException(Error.SENSOR_CHIP_TAGS_IS_EMPTY);
        }
        if (request.name() == null) {
            throw new TraitementException(Error.SENSOR_NAME_IS_EMPTY);
        }
        if (request.structureId() == null) {
            throw new TraitementException(Error.SENSOR_STRUCTURE_ID_IS_EMPTY);
        }
    }

    /**
     * Ensures that the sensor request object contains all necessary information.
     * <br>
     * This method checks that none of the required properties are malformed.
     * If any essential field is malformed, an exception is thrown.
     *
     * @param request An object containing the sensor information.
     * @throws TraitementException If any required property is malformed.
     */
    private void sensorMalformedPrecondition(BaseSensorDTO request) throws TraitementException {
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
            throw new TraitementException(Error.SENSOR_COMMENT_EXCEED_LIMIT);
        }
    }


    /**
     * Service that will handle the edit sensor request
     * @param editSensorRequestDTO The edit sensor request DTO
     */
    public EditSensorResponseDTO editSensor(EditSensorRequestDTO editSensorRequestDTO) throws TraitementException {
        Objects.requireNonNull(editSensorRequestDTO);
        var sensorOptional = sensorRepository.findByChips(editSensorRequestDTO.getControlChip(), editSensorRequestDTO.getMeasureChip());
        if (sensorOptional.isEmpty()){
            throw new TraitementException(Error.SENSOR_ID_NOT_FOUND);
        }
        if (editSensorRequestDTO.getName().length() > 32){
            throw new TraitementException(Error.SENSOR_NAME_EXCEED_LIMIT);
        }
        if (editSensorRequestDTO.getComment().length() > 1000){
            throw new TraitementException(Error.SENSOR_COMMENT_EXCEED_LIMIT);
        }
        if (sensorRepository.findByName(editSensorRequestDTO.getName()).isPresent()){
            throw new TraitementException(Error.SENSOR_NAME_ALREADY_EXISTS);
        }
        var sensor = sensorOptional.get();
        sensor.setName(editSensorRequestDTO.getName());
        var formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        sensor.setInstallationDate(LocalDateTime.parse(editSensorRequestDTO.getInstallationDate(), formatter));
        sensor.setNote(editSensorRequestDTO.getComment());
        sensorRepository.save(sensor);
        return new EditSensorResponseDTO(editSensorRequestDTO.getControlChip(), editSensorRequestDTO.getMeasureChip(), LocalDateTime.now().toString());
    }


}
