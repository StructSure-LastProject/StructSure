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
}
