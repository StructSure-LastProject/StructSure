package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Scan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ScanRepository extends JpaRepository<Scan, Long> {
  List<Scan> findScansByStructureId(Long structureId);
  Scan findScanById(Long id);
}