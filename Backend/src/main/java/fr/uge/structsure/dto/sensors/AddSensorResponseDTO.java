package fr.uge.structsure.dto.sensors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record AddSensorResponseDTO(String controlChip, String measureChip) {
}
