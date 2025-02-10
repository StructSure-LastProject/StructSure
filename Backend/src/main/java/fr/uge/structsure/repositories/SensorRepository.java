
package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.entities.SensorId;
import fr.uge.structsure.entities.Structure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    @Query("SELECT s FROM Sensor s WHERE s.structure.id = :structureId")
    List<Sensor> findByStructureId(Long structureId);

    Optional<Sensor> findBySensorId(SensorId sensorId);

    Optional<Sensor> findByName(String name);
    
    @Query("SELECT s FROM Sensor s WHERE s.sensorId.controlChip = :chipTag OR s.sensorId.measureChip = :chipTag")
    List<Sensor> findByChipTag(String chipTag);

    List<Sensor> findByStructure(Structure structure);
}
