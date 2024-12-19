package fr.uge.structsure.services;

import fr.uge.structsure.dto.sensors.SensorFilterDTO;
import fr.uge.structsure.dto.sensors.SensorResponseDTO;
import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.repositories.SensorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SensorServiceTest {

  @Mock
  private SensorRepository sensorRepository;

  @InjectMocks
  private SensorService sensorService;

  private Sensor mockSensor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // Créer un capteur mock pour les tests
    mockSensor = new Sensor();
    mockSensor.setName("Sensor 1");
    mockSensor.setArchived(false);
    mockSensor.setInstallationDate("2024-12-19T11:46:48.267006039");
    mockSensor.setX(10.0);
    mockSensor.setY(20.0);
    mockSensor.setNote("Test Sensor");
  }

  @Test
  void testGetSensorsByStructureIdWithFilter() {
    // Créer un filtre
    SensorFilterDTO filter = new SensorFilterDTO();
    filter.setFiltreEtat("actif");
    filter.setDateInstallationMin(LocalDate.of(2022, 1, 1));
    filter.setDateInstallationMax(LocalDate.of(2024, 1, 1));
    filter.setTri("dateInstallation");
    filter.setOrdre("desc");

    // Simuler une réponse du repository
    when(sensorRepository.findByStructureId(1L)).thenReturn(List.of(mockSensor));

    List<SensorResponseDTO> result = sensorService.getSensorsByStructureId(1L, filter);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Sensor 1", result.get(0).getName());
  }

  @Test
  void testGetSensorsByStructureIdWithNoSensors() {
    // Simuler une réponse vide du repository
    SensorFilterDTO filter = new SensorFilterDTO();
    when(sensorRepository.findByStructureId(1L)).thenReturn(List.of());

    List<SensorResponseDTO> result = sensorService.getSensorsByStructureId(1L, filter);

    assertNotNull(result);
    assertTrue(result.isEmpty());
  }

  @Test
  void testGetSensorsByStructure() {
    // Simuler une réponse du repository pour les capteurs non archivés
    when(sensorRepository.findByStructureIdAndArchivedFalse(1L)).thenReturn(List.of(mockSensor));

    List<Sensor> result = sensorService.getSensorsByStructure(1L);

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals("Sensor 1", result.get(0).getName());
  }


  @Test
  void testGetSensorsByStructureIdWithInvalidDateRange() {
    // Créer un filtre avec une date de début après la date de fin
    SensorFilterDTO filter = new SensorFilterDTO();
    filter.setDateInstallationMin(LocalDate.of(2024, 1, 1));
    filter.setDateInstallationMax(LocalDate.of(2023, 1, 1));

    // Vérifier que la méthode lance une exception IllegalArgumentException
    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
        sensorService.getSensorsByStructureId(1L, filter)
    );

    assertEquals("La date limite ne peut pas être inférieure à la date supérieure", exception.getMessage());
  }
}
