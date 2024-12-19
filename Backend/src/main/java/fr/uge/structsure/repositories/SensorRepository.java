
package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.entities.SensorId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SensorRepository extends JpaRepository<Sensor, Long> {
    @Query("SELECT s FROM Sensor s WHERE s.structure.id = :structureId")
    List<Sensor> findByStructureId(Long structureId);
    List<Sensor> findByStructureIdAndArchivedFalse(Long structureId);
    Sensor findSensorBySensorId_ControlChipAndSensorId_MeasureChip(String s, String s1);
}