package fr.uge.structsure.dto.scan;

/**
 * The DTO for the scan sensor edit for the android application
 * It contains the sensor id and the edited fields (null if not changed)
 */
public record AndroidSensorEditDTO(
        String sensorId,
        String controlChip,
        String measureChip,
        String name,
        String note
) {}