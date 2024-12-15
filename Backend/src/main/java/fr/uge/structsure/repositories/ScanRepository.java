package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Scan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanRepository extends JpaRepository<Scan, Long> {}