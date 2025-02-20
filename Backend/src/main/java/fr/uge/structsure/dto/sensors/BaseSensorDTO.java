package fr.uge.structsure.dto.sensors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record BaseSensorDTO(Long structureId,
                            String controlChip,
                            String measureChip,
                            String name,
                            String note) {
}
