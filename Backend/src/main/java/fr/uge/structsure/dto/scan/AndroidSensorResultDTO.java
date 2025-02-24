package fr.uge.structsure.dto.scan;

/**
 * The DTO for the sensor result for the android application
 * It contains the sensor id, the control chip, the measure chip, the name, the state, the note, the installation date
 */
public record AndroidSensorResultDTO(
        String sensorId,
        String control_chip,
        String measure_chip,
        String name,
        String state,
        String note,
        String installation_date
) {}
