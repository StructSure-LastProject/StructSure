package fr.uge.structsure.dto.auth;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

@JsonSerialize
public record RegisterResponseDTO(String login) {
    public RegisterResponseDTO {
        Objects.requireNonNull(login);
    }
}
