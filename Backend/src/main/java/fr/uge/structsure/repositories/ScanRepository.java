package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Scan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The repository for the scan entity
 */
@Repository
public interface ScanRepository extends JpaRepository<Scan, Long> {}