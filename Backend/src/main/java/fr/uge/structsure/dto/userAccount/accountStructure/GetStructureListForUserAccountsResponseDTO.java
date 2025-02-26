package fr.uge.structsure.dto.userAccount.accountStructure;

import java.util.List;
import java.util.Objects;

/**
 * Get structure list for user accounts response DTO
 * @param structureDetailsList The list of structure details
 */
public record GetStructureListForUserAccountsResponseDTO(List<StructureDetails> structureDetailsList) {
    /**
     * Constructor
     * @param structureDetailsList The list of structure details
     */
    public GetStructureListForUserAccountsResponseDTO {
        Objects.requireNonNull(structureDetailsList);
    }
}
