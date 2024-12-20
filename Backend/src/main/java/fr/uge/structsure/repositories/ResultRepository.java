package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Result;
import fr.uge.structsure.entities.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
    @Query("""
           SELECT r
           FROM Result r
           WHERE r.sensor = :sensor
           ORDER BY r.id DESC
           LIMIT 1
           """)
    Result findLatestStateBySensor(@Param("sensor") Sensor sensor);
    List<Result> findAllByScanId(Long scanId);
}