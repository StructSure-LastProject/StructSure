package fr.uge.structsure.dto.sensors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record AddSensorAnswerDTO(String controlChip, String measureChip) {
}
