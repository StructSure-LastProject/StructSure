package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Result;
import fr.uge.structsure.entities.Scan;
import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.entities.Structure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
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
     *
     */
    List<Result> findByScan(Scan scan);
}
