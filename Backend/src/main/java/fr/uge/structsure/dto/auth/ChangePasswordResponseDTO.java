package fr.uge.structsure.dto.auth;

import java.util.Objects;

public record ChangePasswordResponseDTO(Long userId) {
    public ChangePasswordResponseDTO {
        Objects.requireNonNull(userId);
    }
}
