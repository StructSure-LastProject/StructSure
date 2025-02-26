package fr.uge.structsure.dto.sensors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.entities.State;

import java.time.LocalDate;

/**
 * The sensor dto
 * @param controlChip the control chip id
 * @param measureChip the measure chip id
 * @param name the name of the sensor
 * @param note the note of the sensor
 * @param state the state of the sensor
 * @param archived true if the sensor is archived and false if not
 * @param installationDate the installation date of the sensor
 * @param plan the id of the plan where the sensor is placed
 * @param x the position x of the sensor
 * @param y the position y of the sensor
 */
@JsonSerialize
public record SensorDTO(
        String controlChip,
        String measureChip,
        String name,
        String note,
        State state,
        Boolean archived,
        LocalDate installationDate,
        Long plan,
        Integer x,
        Integer y
) {

    /**
     * The sensor dto constructor from state as Integer type used in
     * {@link fr.uge.structsure.repositories.SensorRepositoryCriteriaQuery}
     * @param controlChip the control chip id
     * @param measureChip the measure chip id
     * @param name the name of the sensor
     * @param note the note of the sensor
     * @param state the state of the sensor (Integer type)
     * @param archived true if the sensor is archived and false if not
     * @param installationDate the installation date of the sensor
     * @param plan the plan where the sensor is placed
     * @param x the position x of the sensor
     * @param y the position y of the sensor
     */
    public SensorDTO(
        String controlChip,
        String measureChip,
        String name,
        String note,
        Integer state,
        boolean archived,
        LocalDate installationDate,
        Long plan,
        Integer x,
        Integer y
    ) {

        this(controlChip, measureChip, name, note, State.values()[state], archived, installationDate, plan, x, y);
    }

    /**
     * Returns the dto from the entity and the state of the sensor
     * @param sensor the sensor entity
     * @param state the state of the sensor
     */
    public SensorDTO(Sensor sensor, State state) {
        this(
            sensor.getSensorId().getControlChip(),
            sensor.getSensorId().getMeasureChip(),
            sensor.getName(),
            sensor.getNote(),
            state,
            sensor.isArchived(),
            sensor.getInstallationDate(),
            sensor.getPlan().getId(),
            sensor.getX(),
            sensor.getY());
    }
}