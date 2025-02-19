package fr.uge.structsure.services;

import fr.uge.structsure.dto.sensors.AddSensorAnswerDTO;
import fr.uge.structsure.dto.sensors.AddSensorRequestDTO;
import fr.uge.structsure.dto.sensors.AllSensorsByStructureRequestDTO;
import fr.uge.structsure.dto.sensors.SensorDTO;
import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.repositories.ResultRepository;
import fr.uge.structsure.repositories.SensorRepository;
import fr.uge.structsure.repositories.SensorRepositoryCriteriaQuery;
import fr.uge.structsure.repositories.StructureRepository;
import fr.uge.structsure.utils.OrderEnum;
import fr.uge.structsure.utils.StateEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class SensorServiceTest {

    @Mock
    private SensorRepository sensorRepository;

    @Mock
    private SensorRepositoryCriteriaQuery sensorRepositoryCriteriaQuery;

    @Mock
    private StructureRepository structureRepository;

    @Mock
    private ResultRepository resultRepository;

    @InjectMocks
    private SensorService sensorService;



    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // Test getSensors method
    @Test
    void testGetSensors_StructureNotFound() {
        long structureId = 1L;
        when(structureRepository.findById(structureId)).thenReturn(Optional.empty());
        TraitementException exception = assertThrows(TraitementException.class, () -> sensorService.getSensors(structureId,
                new AllSensorsByStructureRequestDTO("STATE",
                        "ASC", null, null, null, null, 0, 5)));
        assertEquals(ErrorIdentifier.STRUCTURE_ID_NOT_FOUND, exception.getErrorIdentifier());
    }

    @Test
    void testGetSensors() throws TraitementException {
        long structureId = 1L;
        var structure = structureRepository.findById(structureId);
        if (structure.isEmpty()) {
            return;
        }
        List<SensorDTO> sensors = sensorService.getSensors(structureId, new AllSensorsByStructureRequestDTO("STATE",
                "ASC", null, null, null, null, 0, 5));
        assertNotNull(sensors);
        assertEquals(1, sensors.size());
    }

    // Test createSensor method
    @Test
    void testCreateSensor_StructureNotFound() {
        AddSensorRequestDTO request = new AddSensorRequestDTO(1L, "chip1", "chip2", "Sensor1", "", "2023-02-01T14:30:00", 1.0, 1.0);
        when(structureRepository.findById(request.structureId())).thenReturn(Optional.empty());

        TraitementException exception = assertThrows(TraitementException.class, () -> sensorService.createSensor(request));
        assertEquals(Error.SENSOR_STRUCTURE_NOT_FOUND, exception.error);
    }

    @Test
    void testCreateSensor_SensorNameAlreadyExists() {
        AddSensorRequestDTO request = new AddSensorRequestDTO(1L, "chip1", "chip2", "Sensor1", "", "2023-02-01T14:30:00", 1.0, 1.0);
        when(structureRepository.findById(request.structureId())).thenReturn(Optional.of(new Structure()));
        when(sensorRepository.findByName(request.name())).thenReturn(Optional.of(new Sensor()));

        TraitementException exception = assertThrows(TraitementException.class, () -> sensorService.createSensor(request));
        assertEquals(Error.SENSOR_NAME_ALREADY_EXISTS, exception.error);
    }

    @Test
    void testCreateSensor_Success() throws TraitementException {
        AddSensorRequestDTO request = new AddSensorRequestDTO(1L, "chip1", "chip2", "Sensor1", "", "2023-02-01T14:30:00", 1.0, 1.0);
        var structure = new Structure();
        when(structureRepository.findById(request.structureId())).thenReturn(Optional.of(structure));
        when(sensorRepository.findByName(request.name())).thenReturn(Optional.empty());
        when(sensorRepository.findByChipTag(request.controlChip())).thenReturn(List.of());
        var formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        Sensor sensor = new Sensor(request.controlChip(), request.measureChip(), request.name(), request.note(), LocalDateTime.parse(request.installationDate(), formatter), request.x(), request.y(), false, structure);
        when(sensorRepository.save(any(Sensor.class))).thenReturn(sensor);
        AddSensorAnswerDTO response = sensorService.createSensor(request);
        assertNotNull(response);
        assertEquals(request.controlChip(), response.controlChip());
        assertEquals(request.measureChip(), response.measureChip());
    }

    // Test sensorEmptyPrecondition (indirectly tested through createSensor)
    @Test
    void testSensorEmptyPrecondition() {
        AddSensorRequestDTO request = new AddSensorRequestDTO(1L, null, "chip2", "Sensor1", "", "2023-02-01", 1.0, 1.0);

        TraitementException exception = assertThrows(TraitementException.class, () -> sensorService.createSensor(request));
        assertEquals(Error.SENSOR_CHIP_TAGS_IS_EMPTY, exception.error);
    }

    // Test sensorMalformedPrecondition (indirectly tested through createSensor)
    @Test
    void testSensorMalformedPrecondition() {
        AddSensorRequestDTO request = new AddSensorRequestDTO(1L, "chip1", "chip2", "Sensor1", "", "invalid-date", 1.0, 1.0);

        TraitementException exception = assertThrows(TraitementException.class, () -> sensorService.createSensor(request));
        assertEquals(Error.SENSOR_INSTALLATION_DATE_INVALID_FORMAT, exception.error);
    }
}
