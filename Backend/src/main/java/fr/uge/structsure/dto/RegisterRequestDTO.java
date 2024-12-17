package fr.uge.structsure.dto;

import fr.uge.structsure.entities.Role;

import java.util.Objects;

public record RegisterRequestDTO(String login, String password, String firstname, String lastname, String role) {

    public RegisterRequestDTO {
        Objects.requireNonNull(login);
        Objects.requireNonNull(password);
        Objects.requireNonNull(firstname);
        Objects.requireNonNull(lastname);
        Objects.requireNonNull(role);
    }
}