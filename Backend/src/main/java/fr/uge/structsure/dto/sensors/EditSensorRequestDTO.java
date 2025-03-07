package fr.uge.structsure.dto.sensors;

import fr.uge.structsure.entities.Sensor;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.utils.DiffMaker;

import java.util.Objects;

/**
 * Edit sensor request DTO
 */
public class EditSensorRequestDTO {
    private final String controlChip;
    private final String measureChip;
    private final String name;
    private final String installationDate;
    private final String note;


    /**
    * Constructor
    * @param name The name of the sensor
    * @param controlChip The control chip
    * @param measureChip The measure chip
    * @param installationDate The installation date of the sensor
    * @param note The note attached to the sensor
    * @throws TraitementException Custom exception
    */
    public EditSensorRequestDTO(String controlChip, String measureChip, String name, String installationDate, String note) throws TraitementException {
        if (controlChip == null || controlChip.isEmpty() || measureChip == null || measureChip.isEmpty()){
            throw new TraitementException(Error.SENSOR_CHIP_TAGS_IS_EMPTY);
        }
        this.controlChip = controlChip;
        this.measureChip = measureChip;
        if (name == null || name.isEmpty()){
            throw new TraitementException(Error.SENSOR_NAME_IS_EMPTY);
        }
        if (name.length() > 32) {
            throw new TraitementException(Error.SENSOR_NAME_EXCEED_LIMIT);
        }
        this.name = name;
        this.installationDate = Objects.requireNonNullElse(installationDate, "");
        if (note != null && note.length() > 1000) {
            throw new TraitementException(Error.SENSOR_COMMENT_EXCEED_LIMIT);
        }
        this.note = Objects.requireNonNullElse(note, "");
    }

    /**
     * Gets the display name of the sensor
     * @return the display name of the sensor
     */
    public String name() {
        return name;
    }

    /**
     * Gets the time at which the sensor got installed on a structure
     * @return the time at which the sensor got installed
     */
    public String installationDate() {
        return installationDate;
    }

    /**
     * Gets the commentary of the structure
     * @return the note of the structure
     */
    public String note() {
        return note;
    }

    /**
     * Gets the ID of the RFID chip that should always respond OK
     * @return the ID of the control chip
     */
    public String controlChip() {
        return controlChip;
    }

    /**
     * Gets the ID of the RFID chip that should never respond OK
     * @return the ID of the measure chip
     */
    public String measureChip() {
        return measureChip;
    }

    /**
     * Creates a list of updated fields ready to be logged.
     * @param sensor the initial data
     * @return the changed values
     */
    public String logDiff(Sensor sensor) {
        return new DiffMaker()
            .add("Nom", sensor.getName(), name)
            .add("Note", sensor.getNote(), note)
            .add("Date d'installation", sensor.getInstallationDate() + "", installationDate)
            .toString();
    }
}
