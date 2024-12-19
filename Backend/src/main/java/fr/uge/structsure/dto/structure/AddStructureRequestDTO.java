package fr.uge.structsure.dto.structure;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

@JsonSerialize
public record AddStructureRequestDTO(String name, String note) {
    public AddStructureRequestDTO {
        Objects.requireNonNull(name);
        Objects.requireNonNull(note);
    }
}
