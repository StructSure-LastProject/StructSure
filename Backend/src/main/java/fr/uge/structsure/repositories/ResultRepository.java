package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Result;
import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.entities.SensorId;
import fr.uge.structsure.entities.State;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResultRepository extends JpaRepository<Result, Long> {
  @Query("""
           SELECT r
           FROM Result r
           WHERE r.sensor = :sensor
           ORDER BY r.id DESC
           """)
  Result findLatestStateBySensorId(@Param("sensor") SensorId sensor);

  List<Result> findAllByScanId(Long scanId);

  State findLatestStateBySensor(Sensor mockSensor);
}