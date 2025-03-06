package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.AppLog;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

/**
 * The repository for the scan entity
 */
@Repository
public interface AppLogRepository extends JpaRepository<AppLog, Long> {

    @Transactional
    long deleteAllByTimeBefore(LocalDateTime time);
}