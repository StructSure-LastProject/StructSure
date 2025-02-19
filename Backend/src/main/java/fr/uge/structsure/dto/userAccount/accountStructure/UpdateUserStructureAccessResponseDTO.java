package fr.uge.structsure.dto.userAccount.accountStructure;

import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) representing a successful response for updating user structure access.
 * <p>
 * This DTO contains the user's login and a list of structure IDs whose access was changed.
 * </p>
 */
public record UpdateUserStructureAccessResponseDTO(String login, List<Long> accessChanged) implements IUpdateUserStructureAccessResponseDTO {

    /**
     * Constructor that ensures both fields are not null.
     * <p>
     * If any of the fields are null, a {@link NullPointerException} will be thrown.
     * </p>
     *
     * @param login The login of the user whose access has been updated.
     * @param accessChanged The list of structure IDs whose access was changed.
     */
    public UpdateUserStructureAccessResponseDTO {
        Objects.requireNonNull(login);
        Objects.requireNonNull(accessChanged);
    }

}
