package fr.uge.structsure.dto.scan;

import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.utils.DiffMaker;

/**
 * The DTO for the scan sensor edit for the android application
 * It contains the id of the sensor and the edited fields (null if not
 * changed)
 */
public record AndroidSensorEditDTO(
    String sensorId,
    String controlChip,
    String measureChip,
    String name,
    String note,
    Long plan,
    Integer x,
    Integer y
) {

    /**
     * Creates a list of updated fields ready to be logged.
     * @param sensor the initial data
     * @return the changed values
     */
    public String logDiff(Sensor sensor) {
        return new DiffMaker()
            .add("Nom", sensor.getName(), name)
            .add("Note", sensor.getNote(), note)
            .toString();
    }
}
