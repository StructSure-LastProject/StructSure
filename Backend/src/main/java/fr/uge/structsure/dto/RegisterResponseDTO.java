package fr.uge.structsure.dto;

import java.util.Objects;

public record RegisterResponseDTO(String login) {
    public RegisterResponseDTO {
        Objects.requireNonNull(login);
    }
}
