package fr.uge.structsure.dto.userAccount;

import java.util.Objects;

public record PasswordRequest(String password) {
    public PasswordRequest {
        Objects.requireNonNull(password);
    }
}
