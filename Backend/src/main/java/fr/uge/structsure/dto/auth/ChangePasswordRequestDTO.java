package fr.uge.structsure.dto.auth;

import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;

import java.util.Objects;

/**
 * DTO for handling a user's password change request.
 *
 * This record encapsulates the necessary data for changing a user's password,
 * including validation to ensure all fields are present and the new password matches its confirmation.
 *
 * @param userId             The unique identifier of the user.
 * @param currentPassword    The user's current password.
 * @param newPassword        The new password the user wants to set.
 * @param confirmNewPassword The confirmation of the new password to ensure accuracy.
 */
public record ChangePasswordRequestDTO(Long userId, String currentPassword, String newPassword, String confirmNewPassword) {

    public void checkFields() throws TraitementException {
        if (Objects.isNull(userId) || Objects.isNull(currentPassword) || Objects.isNull(confirmNewPassword) || Objects.isNull(newPassword)) {
            throw new TraitementException(Error.MISSING_FIELDS);
        }
        if (!newPassword.equals(confirmNewPassword)) {
            throw new TraitementException(Error.PASSWORD_AND_CONFIRMATION_NOT_MATCH);
        }
    }
}
