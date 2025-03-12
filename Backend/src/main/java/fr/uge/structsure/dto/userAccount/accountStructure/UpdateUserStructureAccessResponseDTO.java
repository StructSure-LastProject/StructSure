package fr.uge.structsure.dto.userAccount.accountStructure;

import java.util.List;

/**
 * Data Transfer Object (DTO) representing a successful response for updating user structure access.
 * <p>
 * This DTO contains the user's login and a list of structure IDs whose access was changed.
 * </p>
 * @param login The login.
 * @param accessChanged The list of structure IDs whose access was changed.
 */
public record UpdateUserStructureAccessResponseDTO(String login, List<Long> accessChanged) {}

