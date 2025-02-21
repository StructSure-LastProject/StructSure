package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Result;
import fr.uge.structsure.entities.Scan;
import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.entities.Structure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * The repository for the result entity
 */
@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    long countBySensor(Sensor sensor);

    @Query("""
        SELECT COUNT(result.id) > 0
        FROM Result result
        WHERE result.sensor = :sensor
        AND result.state = fr.uge.structsure.entities.State.NOK
    """
    )
    boolean existsResultWithNokState(Sensor sensor);

    @Query("""
        SELECT COUNT(result.id) > 0
        FROM Result result
        WHERE result.sensor = :sensor
        AND result.state = fr.uge.structsure.entities.State.DEFECTIVE
    """
    )
    boolean existsResultWithDefectiveState(Sensor sensor);

    /**
     * Batch insert results with optimized performance
     */
    @Modifying
    @Query(value = """
        INSERT INTO result (state, scan_id, control_chip, measure_chip)
        VALUES (:#{#result.state}, :#{#result.scan.id}, 
                :#{#result.sensor.sensorId.controlChip}, 
                :#{#result.sensor.sensorId.measureChip})
        """, nativeQuery = true)
    void insertResult(@Param("result") Result result);

    /**
     * Save a list of results in batch
     */
    default void saveAllResults(List<Result> results) {
        for (Result result : results) {
            insertResult(result);
        }
    }
}
