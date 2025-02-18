package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Scan;
import fr.uge.structsure.entities.Structure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The repository for the scan entity
 */
@Repository
public interface ScanRepository extends JpaRepository<Scan, Long> {

    List<Scan> findByStructure(Structure structure);

}