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
  private final String comment;


  /**
   * Constructor
   * @param name The name of the sensor
   * @param controlChip The control chip
   * @param measureChip The measure chip
   * @param installationDate The installation date of the sensor
   * @param comment The comment attached to the sensor
   * @throws TraitementException Custom exception
   */
  public EditSensorRequestDTO(String controlChip, String measureChip, String name, String installationDate, String comment) throws TraitementException {
    if (controlChip == null || controlChip.isEmpty() || measureChip == null || measureChip.isEmpty()){
      throw new TraitementException(Error.SENSOR_CHIP_TAGS_IS_EMPTY);
    }
    if (name == null || name.isEmpty()){
      throw new TraitementException(Error.SENSOR_NAME_IS_EMPTY);
    }
    if (installationDate == null || installationDate.isEmpty()){
      throw new TraitementException(Error.SENSOR_INSTALLATION_DATE_IS_EMPTY);
    }
    if (comment == null){
      throw new TraitementException(Error.SENSOR_COMMENT_IS_EMPTY);
    }
    this.controlChip = controlChip;
    this.measureChip = measureChip;
    this.name = name;
    this.installationDate = installationDate;
    this.comment = comment;
  }

  public String getName() {
    return name;
  }

  public String getInstallationDate() {
    return installationDate;
  }

  public String getComment() {
    return comment;
  }

  public String getControlChip() {
    return controlChip;
  }

  public String getMeasureChip() {
    return measureChip;
  }
}
