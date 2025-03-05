package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.AppLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * The repository for the scan entity
 */
@Repository
public interface AppLogRepository extends JpaRepository<AppLog, Long> {

}