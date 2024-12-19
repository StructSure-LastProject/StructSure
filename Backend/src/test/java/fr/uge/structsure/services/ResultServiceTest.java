package fr.uge.structsure.services;

import fr.uge.structsure.entities.Result;
import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.repositories.ResultRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ResultServiceTest {

  @Mock
  private ResultRepository resultRepository;

  @InjectMocks
  private ResultService resultService;

  private Sensor mockSensor;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // Créer un capteur mock pour les tests
    mockSensor = new Sensor();
    mockSensor.setName("Sensor 1");
  }

  @Test
  void testGetLatestStateByChipWhenResultsFound() {
    // Créer un résultat mock pour ce test
    Result mockResult = new Result();
    mockResult.setSensor(mockSensor);
    mockResult.setState("Active");

    // Simuler la réponse du repository
    when(resultRepository.findLatestStateBySensor(mockSensor));

    Result result = resultService.getLatestStateByChip(mockSensor);

    assertNotNull(result);
    assertEquals("Active", result.getState());
  }

  @Test
  void testGetLatestStateByChipWhenNoResultsFound() {
    // Simuler une réponse vide du repository (aucun résultat pour ce capteur)
    when(resultRepository.findLatestStateBySensor(mockSensor)).thenReturn(List.of());

    Result result = resultService.getLatestStateByChip(mockSensor);

    assertNull(result);
  }
}
