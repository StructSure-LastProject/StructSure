package fr.uge.structsure.dto.sensors;

import java.util.List;
import java.util.Objects;

/**
 * Sensor by structure response DTO
 * @param sizeOfResult The total number of sensors
 * @param sensors The sensor list
 */
public record SensorByStructureResponseDTO(long sizeOfResult, List<SensorDTO> sensors) {
    /**
     * Constructor
     * @param sizeOfResult The total number of sensors
     * @param sensors The sensor list
     */
    public SensorByStructureResponseDTO {
        Objects.requireNonNull(sensors);
    }
}
