package fr.uge.structsure.dto.sensors;

import java.util.Objects;

/**
 * The delete position sensor DTO
 */
public record DeletePositionSensorResponseDTO(String controlChip, String measureChip) {
    public DeletePositionSensorResponseDTO {
        Objects.requireNonNull(controlChip);
        Objects.requireNonNull(measureChip);
    }
}