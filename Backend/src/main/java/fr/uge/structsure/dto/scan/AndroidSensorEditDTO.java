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
        var diff = new DiffMaker();
        if (name != null) diff.add("Nom", sensor.getName(), name);
        if (note != null) diff.add("Note", sensor.getNote(), note);
        return diff
            .add(plan != null && plan == -1, "Plan retiré: #" + sensor.getPlan().getId())
            .add(plan != null && plan != -1, "Plan ajouté: #" + plan + " (x:" + x + ", y:" + y + ")")
            .toString();
    }
}
