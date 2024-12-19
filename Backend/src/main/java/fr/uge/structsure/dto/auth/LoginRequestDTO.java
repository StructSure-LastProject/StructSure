package fr.uge.structsure.dto.auth;

import java.util.Objects;

public record LoginRequestDTO(String login, String password) {
    public LoginRequestDTO {
        Objects.requireNonNull(login);
        Objects.requireNonNull(password);
    }
}
