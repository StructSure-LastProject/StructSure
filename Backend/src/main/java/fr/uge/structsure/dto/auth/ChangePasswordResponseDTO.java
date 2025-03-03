package fr.uge.structsure.dto.auth;

import java.util.Objects;

/**
 * DTO representing the response after a successful password change.
 *
 * This record contains the user ID of the account whose password was updated.
 *
 * @param userId The unique identifier of the user whose password has been changed.
 *               Must not be null.
 */
public record ChangePasswordResponseDTO(Long userId) {
    public ChangePasswordResponseDTO {
        Objects.requireNonNull(userId);
    }
}
