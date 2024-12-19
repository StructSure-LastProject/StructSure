package fr.uge.structsure.services;

import fr.uge.structsure.entities.Scan;
import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.repositories.ScanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class ScanServiceTest {

  @Mock
  private ScanRepository scanRepository;

  @InjectMocks
  private ScanService scanService;

  private Scan mockScan;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    // Créer un mock de Scan pour les tests
    mockScan = new Scan();
    mockScan.setName("Scan 1");
    var structure = new Structure();
    structure.setName("Structure 1");

    mockScan.setName("Scan 1");
  }

  @Test
  void testGetScansByStructureWhenScansFound() {
    // Simuler une liste de scans pour la structureId 1
    when(scanRepository.findScansByStructureId(1L)).thenReturn(List.of(mockScan));

    List<Scan> scans = scanService.getScansByStructure(1L);

    assertNotNull(scans);
    assertFalse(scans.isEmpty());
    assertEquals(1, scans.size());
    assertEquals("Scan 1", scans.get(0));
  }

  @Test
  void testGetScansByStructureWhenNoScansFound() {
    // Simuler une réponse vide pour la structureId 1
    when(scanRepository.findScansByStructureId(1L)).thenReturn(List.of());

    List<Scan> scans = scanService.getScansByStructure(1L);

    assertNotNull(scans);
    assertTrue(scans.isEmpty());
  }
}
