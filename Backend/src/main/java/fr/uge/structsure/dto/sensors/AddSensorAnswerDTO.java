package fr.uge.structsure.dto.sensors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.entities.SensorId;

@JsonSerialize
public record AddSensorAnswerDTO(SensorId id) {

}
