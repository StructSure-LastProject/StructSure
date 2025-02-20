
package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Plan;
import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.entities.SensorId;
import fr.uge.structsure.entities.Structure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * The repository for Sensor entity
 */
@Repository
public interface
SensorRepository extends JpaRepository<Sensor, Long> {
    /**
     * Will find a sensor by its id
     * @param structureId the id of the sensor
     * @return list of the sensors
     */
    @Query("SELECT s FROM Sensor s WHERE s.structure.id = :structureId")
    List<Sensor> findByStructureId(Long structureId);

    /**
     * Will find a sensor by its name
     * @param name the name of the sensor
     * @return optional with the sensor if there is a sensor and optional empty if not
     */
    Optional<Sensor> findByName(String name);

    /**
     * Will find a sensor by its chip tag
     * @param chipTag the chip tag of the sensor
     * @return list of the sensors
     */
    @Query("SELECT s FROM Sensor s WHERE s.sensorId.controlChip = :chipTag OR s.sensorId.measureChip = :chipTag")
    List<Sensor> findByChipTag(String chipTag);

    /**
     * Will find sensors of the structure
     * @param structure the structure
     * @return list of the sensors
     */
    List<Sensor> findByStructure(Structure structure);

    /**
     * Will find sensors prensent in a Plan
     * @param plan the plan
     * @return List<Sensor> list of the sensors
     */
    @Query("""
    SELECT sensor
    FROM Sensor sensor
    JOIN Structure structure ON sensor.structure = structure
    JOIN Plan plan ON plan.structure = structure
    WHERE plan = :plan
    """)
    List<Sensor> findByPlan(Plan plan);

    /**
     * Counts the number of sensors in the structure
     * @param structure the structure
     * @return the number of sensors
     */
    long countByStructure(Structure structure);

    /**
     * Checks if there is a sensor with NOK state
     * @param structure the structure
     * @return true if yes and false if not
     */
    @Query("""
    SELECT COUNT(sensor.sensorId) > 0 
    FROM Sensor sensor
    JOIN Result result ON result.sensor = sensor
    WHERE sensor.structure = :structure
    AND result.state = fr.uge.structsure.entities.State.NOK
    """)
    boolean existsSensorWithNokState(Structure structure);

    /**
     * Checks if there is a sensor with DEFECTIVE state
     * @param structure the structure
     * @return true if yes and false if not
     */
    @Query("""
        SELECT COUNT(sensor.sensorId) > 0 
        FROM Sensor sensor
        JOIN Result result ON result.sensor = sensor
        WHERE sensor.structure = :structure
        AND result.state = fr.uge.structsure.entities.State.DEFECTIVE
    """)
    boolean existsSensorWithDefectiveState(Structure structure);

}
