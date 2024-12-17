package fr.uge.structsure.dto;

import java.util.Objects;

public record ErrorDTO(String error) {
    public ErrorDTO {
        Objects.requireNonNull(error);
    }
}
