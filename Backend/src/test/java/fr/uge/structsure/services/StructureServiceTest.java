package fr.uge.structsure.services;

import fr.uge.structsure.dto.StructureDTO;
import fr.uge.structsure.dto.structure.AllStructureResponseDTO;
import fr.uge.structsure.dto.structure.GetAllStructureRequest;
import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.repositories.PlanRepository;
import fr.uge.structsure.repositories.SensorRepository;
import fr.uge.structsure.repositories.StructureRepository;
import fr.uge.structsure.utils.OrderEnum;
import fr.uge.structsure.utils.SortEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class StructureServiceTest {

  @Mock
  private StructureRepository structureRepository;

  @Mock
  private PlanRepository planRepository;

  @Mock
  private SensorRepository sensorRepository;

  @InjectMocks
  private StructureService structureService;

  private Structure mockStructure;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // Créer une structure mock pour les tests
    mockStructure = new Structure("Structure 1", "Test Structure", false);
  }

  @Test
  void testCreateStructure() {
    // Simuler la sauvegarde d'une structure
    StructureDTO structureDTO = new StructureDTO("Structure 1", "Test Structure", false);
    when(structureRepository.save(any(Structure.class))).thenReturn(mockStructure);

    Structure result = structureService.createStructure(structureDTO, false);

    assertNotNull(result);
    assertEquals("Structure 1", result.getName());
    assertEquals("Test Structure", result.getNote());
    verify(structureRepository, times(1)).save(any(Structure.class));
  }

  @Test
  void testEditStructure() {
    // Simuler la récupération d'une structure existante
    StructureDTO structureDTO = new StructureDTO("Updated Structure", "Updated Note", false);
    when(structureRepository.findById(1L)).thenReturn(Optional.of(mockStructure));
    when(structureRepository.save(any(Structure.class))).thenReturn(mockStructure);

    Structure result = structureService.editStructure(1L, structureDTO);

    assertNotNull(result);
    assertEquals("Updated Structure", result.getName());
    assertEquals("Updated Note", result.getNote());
    verify(structureRepository, times(1)).findById(1L);
    verify(structureRepository, times(1)).save(any(Structure.class));
  }

  @Test
  void testEditStructureNotFound() {
    // Simuler la situation où la structure n'est pas trouvée
    StructureDTO structureDTO = new StructureDTO("Updated Structure", "Updated Note", false);
    when(structureRepository.findById(1L)).thenReturn(Optional.empty());

    Structure result = structureService.editStructure(1L, structureDTO);

    assertNotNull(result);
    assertEquals("", result.getName());
    assertEquals("", result.getNote());
    verify(structureRepository, times(1)).findById(1L);
  }

  @Test
  void testGetAllStructure() {
    // Simuler une liste de structures
    GetAllStructureRequest request = new GetAllStructureRequest( "name", SortEnum.NAME,OrderEnum.ASC );
    when(structureRepository.findAll()).thenReturn(List.of(mockStructure));
    when(planRepository.countByStructureId(anyLong())).thenReturn(1);
    when(sensorRepository.findByStructureId(anyLong())).thenReturn(List.of());

    List<AllStructureResponseDTO> result = structureService.getAllStructure(request);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Structure 1", result.get(0).name());
  }

  @Test
  void testGetAllActiveStructures() {
    // Simuler une liste de structures actives
    when(structureRepository.findByArchivedFalse()).thenReturn(List.of(mockStructure));

    List<Structure> result = structureService.getAllActiveStructures();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Structure 1", result.get(0).getName());
  }
}
