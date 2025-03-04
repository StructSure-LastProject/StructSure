package fr.uge.structsure.services;

import fr.uge.structsure.dto.sensors.*;
import fr.uge.structsure.entities.*;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This class regroups all the services available for the sensor
 */
@Service
public class SensorService {
    private final SensorRepository sensorRepository;
    private final StructureRepository structureRepository;
    private final ResultRepository resultRepository;
    private final PlanRepository planRepository;
    private final AccountRepository accountRepository;
    private final SensorRepositoryCriteriaQuery sensorRepositoryCriteriaQuery;

    private final AuthValidationService authValidationService;

    /**
     * Initialise the sensor service
     * @param sensorRepository the sensor repository
     * @param structureRepository the structure repository
     * @param resultRepository the result repository
     * @param planRepository the plan repository
     * @param accountRepository The account repository
     * @param sensorRepositoryCriteriaQuery the sensor repository using criteria query api
     * @param authValidationService The auth validation service
     */
    @Autowired
    public SensorService(SensorRepository sensorRepository, StructureRepository structureRepository, ResultRepository resultRepository, PlanRepository planRepository,
        AccountRepository accountRepository,SensorRepositoryCriteriaQuery sensorRepositoryCriteriaQuery, AuthValidationService authValidationService) {
        this.sensorRepository = sensorRepository;
        this.structureRepository = structureRepository;
        this.resultRepository = resultRepository;
        this.planRepository = planRepository;
        this.accountRepository = accountRepository;
        this.sensorRepositoryCriteriaQuery = sensorRepositoryCriteriaQuery;
        this.authValidationService = authValidationService;
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
     * Count sensors
     * @param structureId the structure id
     * @param request The request DTO
     * @return long The total number of sensors
     * @throws TraitementException throw if structure not found or DATE_FORMAT_ERROR if there is an error while converting date
     */
    public long countSensors(long structureId, AllSensorsByStructureRequestDTO request) throws TraitementException {
        request.checkFields();
        var structure = structureRepository.findById(structureId);
        if (structure.isEmpty()) {
            throw new TraitementException(Error.STRUCTURE_ID_NOT_FOUND);
        }
        return sensorRepositoryCriteriaQuery.countSensorsByStructureId(structureId, request);
    }

    /**
     * Returns the list of sensors present in a plan
     * @param structureId the structure id
     * @param planId the plan id
     * @param scanId the optional scan id
     * @return List<SensorDTO> the list of the sensors
     */
    public List<SensorDTO> getSensorsByPlanId(long structureId, long planId, Optional<Long> scanId) throws TraitementException {
        var structure = structureRepository.findById(structureId).orElseThrow(() -> new TraitementException(Error.STRUCTURE_ID_NOT_FOUND));
        var plan = planRepository.findByStructureAndId(structure, planId).orElseThrow(() -> new TraitementException(Error.PLAN_NOT_FOUND));
        var sensors = sensorRepository.findByPlan(plan);
        if (scanId.isPresent()) {
            var scanResults = resultRepository.findByScanId(scanId.get());
            var statesBySensor = new HashMap<Sensor, State>();
            for (Result result : scanResults) {
                if (result.getSensor() != null) {
                    statesBySensor.put(result.getSensor(), result.getState());
                }
            }
            return sensors.stream()
                    .map(sensor -> {
                        var state = statesBySensor.getOrDefault(sensor, State.UNKNOWN);
                        return new SensorDTO(sensor, state);
                    })
                    .toList();
        }
        return sensors.stream().map(sensor -> new SensorDTO(sensor, getSensorState(sensor))).toList();
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
    void addPlanAsserts(BaseSensorDTO request) throws TraitementException {
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
        var sensor = sensorRepository.findByChipsId(editSensorRequestDTO.controlChip(), editSensorRequestDTO.measureChip()).orElseThrow(() -> new TraitementException(Error.SENSOR_NOT_FOUND));
        if (!sensor.getName().equals(editSensorRequestDTO.name())){
            if (sensorRepository.nameAlreadyExists(editSensorRequestDTO.name())) {
                throw new TraitementException(Error.SENSOR_NAME_ALREADY_EXISTS);
            }
            sensor.setName(editSensorRequestDTO.name());
        }
        sensor.setNote(editSensorRequestDTO.note());
        if (editSensorRequestDTO.installationDate() != null && !editSensorRequestDTO.installationDate().isEmpty()){
            var formatter = DateTimeFormatter.ISO_LOCAL_DATE;
            sensor.setInstallationDate(LocalDate.parse(editSensorRequestDTO.installationDate(), formatter));
        }
        if (editSensorRequestDTO.installationDate() != null &&
                sensor.getInstallationDate() != null &&
                !sensor.getInstallationDate().format(DateTimeFormatter.ISO_LOCAL_DATE).isEmpty() &&
                editSensorRequestDTO.installationDate().isEmpty()){
            sensor.setInstallationDate(null);
        }
        var sensorSaved = sensorRepository.save(sensor);
        return new EditSensorResponseDTO(sensorSaved.getSensorId().getControlChip(), sensorSaved.getSensorId().getMeasureChip(), LocalDateTime.now().toString());
    }


    /**
     * Get the plan associated to a sensor
     * @param controlChip The control chip od the sensor
     * @param measureChip The measure chip of the sensor
     * @return The Plan associated to the sensor
     */
    public Plan getPlanFromSensor(String controlChip, String measureChip) throws TraitementException {
        Objects.requireNonNull(controlChip);
        Objects.requireNonNull(measureChip);
        var sensor = sensorRepository.findByChipsId(controlChip, measureChip).orElseThrow(() -> new TraitementException(Error.SENSOR_NOT_FOUND));
        return sensor.getPlan();
    }

    /**
     * Positions a sensor on a specified plan within a given structure.
     *
     * @param request The {@link SensorPositionRequestDTO} containing the structure ID, plan ID, sensor chip IDs, and coordinates.
     * @return A {@link SensorPositionResponseDTO} containing the sensor's control chip and measure chip.
     * @throws TraitementException If:
     *         <ul>
     *           <li>The structure is not found ({@code SENSOR_STRUCTURE_NOT_FOUND}).</li>
     *           <li>The plan is not found ({@code PLAN_NOT_FOUND}).</li>
     *           <li>The plan does not belong to the specified structure ({@code PLAN_NOT_BELONG_TO_STRUCTURE}).</li>
     *           <li>The sensor is not found ({@code SENSOR_NOT_FOUND}).</li>
     *         </ul>
     */
    public SensorPositionResponseDTO positionSensor(SensorPositionRequestDTO request) throws TraitementException {
        request.checkFields();
        structureRepository.findById(request.structureId()).orElseThrow(() -> new TraitementException(Error.SENSOR_STRUCTURE_NOT_FOUND));
        var plan = planRepository.findById(request.planId()).orElseThrow(() -> new TraitementException(Error.PLAN_NOT_FOUND));
        if (plan.getStructure().getId() != request.structureId()) {
            throw new TraitementException(Error.PLAN_NOT_BELONG_TO_STRUCTURE);
        }
        var sensor = sensorRepository.findByChipsId(request.controlChip(), request.measureChip()).orElseThrow(() -> new TraitementException(Error.SENSOR_NOT_FOUND));
        if (plan.getSensors() == null) {
            plan.setSensors(new HashSet<>());
        }
        sensor.setX(request.x());
        sensor.setY(request.y());
        plan.getSensors().add(sensor);
        sensor.setPlan(plan);
        planRepository.save(plan);
        sensorRepository.save(sensor);
        return new SensorPositionResponseDTO(request.controlChip(), request.measureChip());
    }

    /**
     * Deletes the position information of a sensor identified by the given control and measure chips.
     *
     * @param controlChip The identifier of the control chip.
     * @param measureChip The identifier of the measure chip.
     * @return A {@link DeletePositionSensorResponseDTO} containing the control and measure chip identifiers.
     * @throws TraitementException If the input fields are invalid or the sensor is not found.
     */
    public DeletePositionSensorResponseDTO deletePositionOfSensor(String controlChip, String measureChip) throws TraitementException {
        if (Objects.isNull(controlChip) || Objects.isNull(measureChip)
                || controlChip.isEmpty() || measureChip.isEmpty()) {
            throw new TraitementException(Error.INVALID_FIELDS);
        }
        var sensor = sensorRepository.findByChipsId(controlChip, measureChip);
        if (sensor.isEmpty()) {
            throw new TraitementException(Error.SENSOR_NOT_FOUND);
        }
        var existingSensor = sensor.get();
        existingSensor.setX(null);
        existingSensor.setY(null);
        existingSensor.setPlan(null);
        sensorRepository.save(existingSensor);
        return new DeletePositionSensorResponseDTO(controlChip, measureChip);
    }


    /**
     * Service that will archive or restore a sensor
     * @param archiveSensorRequestDTO The archive sensor request DTO
     * @param httpServletRequest The http servlet request
     * @return The response DTO
     * @throws TraitementException thrown custom exceptions
     */
    public EditSensorResponseDTO archiveASensor(ArchiveSensorRequestDTO archiveSensorRequestDTO, HttpServletRequest httpServletRequest, boolean isArchived) throws TraitementException {
        archiveSensorRequestDTO.checkFields();
        Objects.requireNonNull(httpServletRequest);
        var accountSession = authValidationService.checkTokenValidityAndUserAccessVerifier(httpServletRequest, accountRepository);
        if (accountSession.getRole() == Role.OPERATEUR || (accountSession.getRole() != Role.RESPONSABLE && accountSession.getRole() != Role.ADMIN)) {
            throw new TraitementException(Error.UNAUTHORIZED_OPERATION);
        }
        var sensor = sensorRepository.findByChipsId(archiveSensorRequestDTO.controlChip(), archiveSensorRequestDTO.measureChip()).orElseThrow(() -> new TraitementException(Error.SENSOR_NOT_FOUND));
        sensor.setArchived(isArchived);
        sensorRepository.save(sensor);
        return new EditSensorResponseDTO(sensor.getSensorId().getControlChip(), sensor.getSensorId().getMeasureChip(), LocalDateTime.now().toString());
    }

}
