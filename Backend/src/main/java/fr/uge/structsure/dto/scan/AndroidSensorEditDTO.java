package fr.uge.structsure.dto.scan;

/**
 * The DTO for the scan sensor edit for the android application
 * It contains the id of the sensor and the edited fields (null if not
 * changed)
 */
public record AndroidSensorEditDTO(
    String sensorId,
    String note,
    Long plan,
    Integer x,
    Integer y
) {}
