package fr.uge.structsure.dto.plan;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record PlanMetadataDTO(String section, String name) {
}
