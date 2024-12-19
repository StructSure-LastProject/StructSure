package fr.uge.structsure.dto.auth;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

@JsonSerialize
public record LoginRequestDTO(String login, String password) {
    public LoginRequestDTO {
        Objects.requireNonNull(login);
        Objects.requireNonNull(password);
    }
}
