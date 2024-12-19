package fr.uge.structsure.services;

import fr.uge.structsure.entities.Scan;
import fr.uge.structsure.repositories.ScanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScanService {

  private final ScanRepository scanRepository;

  public ScanService(ScanRepository scanRepository) {
    this.scanRepository = scanRepository;
  }

  public List<Scan> getScansByStructure(Long structureId) {
    return scanRepository.findScansByStructureId(structureId);
  }
}
