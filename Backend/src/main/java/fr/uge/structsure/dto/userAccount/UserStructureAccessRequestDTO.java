package fr.uge.structsure.dto.userAccount;

import fr.uge.structsure.dto.userAccount.accountStructure.StructurePermission;

import java.util.List;
import java.util.Objects;

/**
 * Data Transfer Object (DTO) representing the request for user structure access.
 * <p>
 * This DTO contains a list of {@link StructurePermission} that specify the user's access permissions
 * to various structures. It ensures that the list of access permissions is not null when the object is created.
 * </p>
 */
public record UserStructureAccessRequestDTO(List<StructurePermission> access) {
    /**
     * Constructor that ensures the provided list of access permissions is not null.
     * <p>
     * If the access list is null, a {@link NullPointerException} will be thrown.
     * </p>
     *
     * @param access The list of structure permissions for the user.
     */
    public UserStructureAccessRequestDTO {
        Objects.requireNonNull(access);
    }
}
