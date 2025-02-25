package fr.uge.structsure.dto.sensors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.exceptions.TraitementException;

/**
 * Represents the dto for the request of adding the position to the sensor in a plan
 */
@JsonSerialize
public record SensorPositionResponseDTO(String controlChip, String measureChip) {

}
