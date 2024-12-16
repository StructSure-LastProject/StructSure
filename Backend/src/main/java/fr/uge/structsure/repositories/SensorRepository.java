
package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
    Sensor findSensorBySensorId_ControlChipAndSensorId_MeasureChip(String sensorId_controlChip, String sensorId_measureChip);
}