package fr.uge.structsure.dto.auth;

import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;

import java.util.Objects;

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
