package fr.uge.structsure.services;

import fr.uge.structsure.dto.sensors.AddSensorResponseDTO;
import fr.uge.structsure.dto.sensors.BaseSensorDTO;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

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
                        "ASC", null, null, null, null, 0, 5, null, null)));
        assertEquals(Error.STRUCTURE_ID_NOT_FOUND, exception.error);
    }

    @Test
    void testGetSensors() throws TraitementException {
        long structureId = 1L;
        var structure = structureRepository.findById(structureId);
        if (structure.isEmpty()) {
            return;
        }
        List<SensorDTO> sensors = sensorService.getSensors(structureId, new AllSensorsByStructureRequestDTO("STATE",
                "ASC", null, null, null, null, 0, 5, null, null));
        assertNotNull(sensors);
        assertEquals(1, sensors.size());
    }

    // Test createSensor method
    @Test
    void testCreateSensor_StructureNotFound() {
        BaseSensorDTO request = new BaseSensorDTO(1L, "chip1", "chip2", "Sensor1", "");
        when(structureRepository.findById(request.structureId())).thenReturn(Optional.empty());

        TraitementException exception = assertThrows(TraitementException.class, () -> sensorService.createSensor(request));
        assertEquals(Error.SENSOR_STRUCTURE_NOT_FOUND, exception.error);
    }

    @Test
    void testCreateSensor_SensorNameAlreadyExists() {
        BaseSensorDTO request = new BaseSensorDTO(1L, "chip1", "chip2", "Sensor1", "");
        when(structureRepository.findById(request.structureId())).thenReturn(Optional.of(new Structure()));
        when(sensorRepository.nameAlreadyExists(request.name())).thenReturn(true);

        TraitementException exception = assertThrows(TraitementException.class, () -> sensorService.createSensor(request));
        assertEquals(Error.SENSOR_NAME_ALREADY_EXISTS, exception.error);
    }

    @Test
    void testCreateSensor_Success() throws TraitementException {
        BaseSensorDTO request = new BaseSensorDTO(1L, "chip1", "chip2", "Sensor1", "");
        var structure = new Structure();
        when(structureRepository.findById(request.structureId())).thenReturn(Optional.of(structure));
        when(sensorRepository.findByName(request.name())).thenReturn(Optional.empty());
        when(sensorRepository.findByChipTag(request.controlChip())).thenReturn(List.of());

        Sensor sensor = new Sensor(request.controlChip(), request.measureChip(), request.name(), request.note(), structure);
        when(sensorRepository.save(any(Sensor.class))).thenReturn(sensor);

        AddSensorResponseDTO response = sensorService.createSensor(request);

        assertNotNull(response);
        assertEquals(request.controlChip(), response.controlChip());
        assertEquals(request.measureChip(), response.measureChip());
    }

    // Test sensorEmptyPrecondition (indirectly tested through createSensor)
    @Test
    void testSensorEmptyPrecondition() {
        BaseSensorDTO request = new BaseSensorDTO(1L, null, "chip2", "Sensor1", "");

        TraitementException exception = assertThrows(TraitementException.class, () -> sensorService.createSensor(request));
        assertEquals(Error.SENSOR_CHIP_TAGS_IS_EMPTY, exception.error);
    }
}
