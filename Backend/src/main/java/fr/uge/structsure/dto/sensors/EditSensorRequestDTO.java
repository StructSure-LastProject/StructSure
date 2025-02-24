package fr.uge.structsure.dto.sensors;

import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;

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
    if (installationDate == null || installationDate.isEmpty()){
      throw new TraitementException(Error.SENSOR_INSTALLATION_DATE_IS_EMPTY);
    }
    this.installationDate = installationDate;
    if (note != null && note.length() > 1000) {
      throw new TraitementException(Error.SENSOR_COMMENT_EXCEED_LIMIT);
    }
    this.note = Objects.requireNonNullElse(note, "");
  }

  public String name() {
    return name;
  }

  public String installationDate() {
    return installationDate;
  }

  public String note() {
    return note;
  }

  public String controlChip() {
    return controlChip;
  }

  public String measureChip() {
    return measureChip;
  }
}
