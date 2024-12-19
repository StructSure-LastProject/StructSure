package fr.uge.structsure.dto;

import java.util.Objects;

public record LoginResponseDTO(String token, String type, String login, String firstName,
                               String lastName, String mail, String role) {
    public LoginResponseDTO {
        Objects.requireNonNull(token);
        Objects.requireNonNull(type);
        Objects.requireNonNull(login);
        Objects.requireNonNull(firstName);
        Objects.requireNonNull(lastName);
        Objects.requireNonNull(mail);
        Objects.requireNonNull(role);
    }
}
