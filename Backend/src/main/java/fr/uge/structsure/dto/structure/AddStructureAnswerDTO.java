package fr.uge.structsure.dto.structure;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

@JsonSerialize
public record AddStructureAnswerDTO(long id, String createdAt) {
    public AddStructureAnswerDTO {
        Objects.requireNonNull(createdAt);
        if (id <= 0) {
            throw new IllegalArgumentException("id must be greater than 0");
        }
    }
}
