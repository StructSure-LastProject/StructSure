package fr.uge.structsure.dto.plan;

import java.util.Objects;

public record AddPlanResponseDTO (long id, String createdAt) {
    public AddPlanResponseDTO {
        Objects.requireNonNull(createdAt);
        if (id <= 0) {
            throw new IllegalArgumentException("id must be greater than 0");
        }
    }
}
