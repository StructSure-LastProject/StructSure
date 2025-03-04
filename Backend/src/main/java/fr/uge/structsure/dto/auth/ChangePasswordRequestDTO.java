package fr.uge.structsure.dto.auth;

import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;

import java.util.Objects;

/**
 * DTO for handling a user's password change request.
 *
 * This record encapsulates the necessary data for changing a user's password,
 * including validation to ensure all fields are present.
 *
 * @param userId             The unique identifier of the user.
 * @param currentPassword    The user's current password.
 * @param newPassword        The new password the user wants to set.
 */
public record ChangePasswordRequestDTO(Long userId, String currentPassword, String newPassword) {

    /**
     * Checks the validity of the fields related to password update.
     * <p>
     * This method ensures that all required fields (`userId`,
     * `currentPassword` and `newPassword`) are not null.
     * </p>
     *
     * @throws TraitementException if any of the required fields are missing
     *                             (throws {@code Error.MISSING_FIELDS}).
     */
    public void checkFields() throws TraitementException {
        if (Objects.isNull(userId) || Objects.isNull(currentPassword) || Objects.isNull(newPassword)) {
            throw new TraitementException(Error.MISSING_FIELDS);
        }
    }
}
