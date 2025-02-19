package fr.uge.structsure.dto.userAccount.accountStructure;

import java.util.List;
import java.util.Objects;

/**
 * Interface that represents the update user structure access response
 */
public interface UpdateUserStructureAccessResponseDTO {
    /**
     * Constructor that ensures all fields are not null.
     * <p>
     * If any of the fields is null, a {@link NullPointerException} will be thrown.
     * </p>
     *
     * @param error The error message indicating the failure reason.
     * @param accessChanged The list of structure IDs whose access was changed.
     * @param accessUnChanged The list of structure IDs whose access could not be changed.
     */
    static UpdateUserStructureAccessResponseDTO error(String error, List<Long> accessChanged, List<Long> accessUnChanged) {
        return new ErrorUpdateUserStructureAccessResponseDTO(
            Objects.requireNonNull(error),
            Objects.requireNonNull(accessChanged),
            Objects.requireNonNull(accessUnChanged)
        );
    }

    static UpdateUserStructureAccessResponseDTO success(String login, List<Long> accessChanged) {
        return new SuccessUpdateUserStructureAccessResponseDTO(
                Objects.requireNonNull(login),
                Objects.requireNonNull(accessChanged)
        );
    }
}

/**
 * Data Transfer Object (DTO) representing an error response for updating user structure access.
 * <p>
 * This DTO contains an error message, a list of structure IDs whose access was changed,
 * and a list of structure IDs whose access could not be changed.
 * </p>
 */
record ErrorUpdateUserStructureAccessResponseDTO(String error, List<Long> accessChanged, List<Long> accessUnChanged) implements UpdateUserStructureAccessResponseDTO {}


/**
 * Data Transfer Object (DTO) representing a successful response for updating user structure access.
 * <p>
 * This DTO contains the user's login and a list of structure IDs whose access was changed.
 * </p>
 */
record SuccessUpdateUserStructureAccessResponseDTO(String login, List<Long> accessChanged) implements UpdateUserStructureAccessResponseDTO {}
