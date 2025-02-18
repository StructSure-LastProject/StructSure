package fr.uge.structsure.dto.sensors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.utils.StateEnum;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@JsonSerialize
public record SensorDTO(
        String controlChip,
        String measureChip,
        String name,
        String note,
        String state,
        boolean archived,
        LocalDateTime installationDate,
        double x,
        double y
) {
    /**
     * Returns the dto from the entity and the state of the sensor
     * @param sensor the sensor entity
     * @param state the state of the sensor
     * @return the dto for the sensor
     */
    public static SensorDTO fromEntityAndState(Sensor sensor, String state) {
        return new SensorDTO(
                sensor.getSensorId().getControlChip(),
                sensor.getSensorId().getMeasureChip(),
                sensor.getName(),
                sensor.getNote(),
                state,
                sensor.getArchived(),
                sensor.getInstallationDate(),
                sensor.getX(),
                sensor.getY()
        );
    }
}
