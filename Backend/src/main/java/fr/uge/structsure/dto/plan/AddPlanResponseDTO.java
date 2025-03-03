package fr.uge.structsure.dto.plan;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

@JsonSerialize
public record AddPlanResponseDTO (long id, String createdAt) {
    public AddPlanResponseDTO {
        Objects.requireNonNull(createdAt);
        if (id <= 0) {
            throw new IllegalArgumentException("id must be greater than 0");
        }
    }
}
