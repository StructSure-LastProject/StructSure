package fr.uge.structsure.dto.auth;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

@JsonSerialize
public record LoginResponseDTO(String token, String type) {
    public LoginResponseDTO {
        Objects.requireNonNull(token);
        Objects.requireNonNull(type);
    }
}
