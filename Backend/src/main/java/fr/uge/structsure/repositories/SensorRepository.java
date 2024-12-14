
package com.example.springbootapi.repositories;

import com.example.springbootapi.entities.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SensorRepository extends JpaRepository<Sensor, Long> {
    Sensor findSensorBySensorId_ControlChipAndSensorId_MeasureChip(String sensorId_controlChip, String sensorId_measureChip);
}