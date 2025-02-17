package fr.uge.structsure.dto.scan;
public record AndroidSensorResultDTO(
        String sensorId,
        String control_chip,
        String measure_chip,
        String name,
        String state,
        String note,
        String installation_date
) {}
