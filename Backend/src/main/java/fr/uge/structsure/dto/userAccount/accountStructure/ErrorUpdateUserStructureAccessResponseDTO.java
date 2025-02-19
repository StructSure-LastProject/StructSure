package fr.uge.structsure.dto.userAccount.accountStructure;

import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) representing an error response for updating user structure access.
 * <p>
 * This DTO contains an error message, a list of structure IDs whose access was changed,
 * and a list of structure IDs whose access could not be changed.
 * </p>
 */
public record ErrorUpdateUserStructureAccessResponseDTO(String error, List<Long> accessChanged, List<Long> accessUnChanged) implements IUpdateUserStructureAccessResponseDTO {

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
    public ErrorUpdateUserStructureAccessResponseDTO {
        Objects.requireNonNull(error);
        Objects.requireNonNull(accessChanged);
        Objects.requireNonNull(accessUnChanged);
    }
}
