package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.AppLog;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * The repository for the scan entity
 */
@Repository
public interface AppLogRepository extends JpaRepository<AppLog, Long> {

    /**
     * Deletes all the log entries that are older than the given time.
     * @param time the time before which entries must be deleted
     * @return the number of removed items
     */
    @Transactional
    long deleteAllByTimeBefore(LocalDateTime time);

    /**
     * Gets all the log entries that match the given search string,
     * sorted by time and limited to the given pagination.
     * @param search the search string
     * @param page the page to get
     * @return the list of matching log entries
     */
    @Query("""
    SELECT l
    FROM AppLog l
    WHERE LOWER(l.author.login) LIKE :search
        OR LOWER(l.author.firstname) LIKE :search
        OR LOWER(l.author.lastname) LIKE :search
        OR LOWER(l.message) LIKE :search
    """)
    List<AppLog> search(@Param("search") String search, Pageable page);

    /**
     * Gets all the log entries sorted by time and limited to the given pagination.
     * @param page the page to get
     * @return the list of matching log entries
     */
    @Query("""
    SELECT l
    FROM AppLog l
    WHERE true
    """)
    List<AppLog> search(Pageable page);

    /**
     * Count the number of log entries that match the given search
     * string ignoring case.
     * @param search the search string
     * @return the number of matching results
     */
    @Query("""
    SELECT count(l)
    FROM AppLog l
    WHERE LOWER(l.author.login) LIKE :search
        OR LOWER(l.author.firstname) LIKE :search
        OR LOWER(l.author.lastname) LIKE :search
        OR LOWER(l.message) LIKE :search
    """)
    Long count(@Param("search") String search);
}