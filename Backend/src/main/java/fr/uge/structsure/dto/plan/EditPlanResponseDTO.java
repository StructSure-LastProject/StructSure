package fr.uge.structsure.dto.plan;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record EditPlanResponseDTO(long id) {
    public EditPlanResponseDTO {
        if (id <= 0) {
            throw new IllegalArgumentException("id must be greater than 0");
        }
    }
}
