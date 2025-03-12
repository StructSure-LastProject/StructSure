package fr.uge.structsure.dto.sensors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Optional;

/**
 * The basic Sensor DTO, used to create one without placing it
 * @param structureId the structure id
 * @param controlChip the control chip
 * @param measureChip the measure chip
 * @param name the name of the sensor
 * @param installationDate an optional installation date
 * @param note the name of the sensor
 */
@JsonSerialize
public record BaseSensorDTO(Long structureId,
                            String controlChip,
                            String measureChip,
                            String name,
                            Optional<String> installationDate,
                            String note) {
}
