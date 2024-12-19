package fr.uge.structsure.dto.auth;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

@JsonSerialize
public record RegisterRequestDTO(String login, String password, String firstname, String lastname, String role) {
    public RegisterRequestDTO {
        Objects.requireNonNull(login);
        Objects.requireNonNull(password);
        Objects.requireNonNull(firstname);
        Objects.requireNonNull(lastname);
        Objects.requireNonNull(role);
    }
}
