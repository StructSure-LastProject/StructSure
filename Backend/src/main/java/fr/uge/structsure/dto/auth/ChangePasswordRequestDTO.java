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

    /**
     * Checks the validity of the fields related to password update.
     * <p>
     * This method ensures that all required fields (`userId`, `currentPassword`,
     * `newPassword`, and `confirmNewPassword`) are not null. It also verifies that
     * the new password and its confirmation match.
     * </p>
     *
     * @throws TraitementException if any of the required fields are missing
     *                             (throws {@code Error.MISSING_FIELDS}), or if the
     *                             new password and confirmation do not match
     *                             (throws {@code Error.PASSWORD_AND_CONFIRMATION_NOT_MATCH}).
     */
    public void checkFields() throws TraitementException {
        if (Objects.isNull(userId) || Objects.isNull(currentPassword) || Objects.isNull(confirmNewPassword) || Objects.isNull(newPassword)) {
            throw new TraitementException(Error.MISSING_FIELDS);
        }
        if (!newPassword.equals(confirmNewPassword)) {
            throw new TraitementException(Error.PASSWORD_AND_CONFIRMATION_NOT_MATCH);
        }
    }
}
