package fr.uge.structsure.dto.sensors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * The Response DTO for adding a Sensor
 * @param controlChip the control chip
 * @param measureChip the measure chip
 */
@JsonSerialize
public record AddSensorResponseDTO(String controlChip, String measureChip) {
}
