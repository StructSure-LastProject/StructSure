package fr.uge.structsure.dto;

import java.util.Objects;

public record LoginResponseDTO(String token, String type) {
    public LoginResponseDTO {
        Objects.requireNonNull(token);
        Objects.requireNonNull(type);
    }
}
