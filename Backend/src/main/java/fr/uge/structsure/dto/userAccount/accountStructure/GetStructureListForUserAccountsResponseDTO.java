package fr.uge.structsure.dto.userAccount.accountStructure;

import java.util.List;
import java.util.Objects;

/**
 * Get structure list for user accounts response DTO
 * @param structureAcessDetailsList The list of structure details
 */
public record GetStructureListForUserAccountsResponseDTO(List<StructureAcessDetails> structureAcessDetailsList) {
    /**
     * Constructor
     * @param structureAcessDetailsList The list of structure details
     */
    public GetStructureListForUserAccountsResponseDTO {
        Objects.requireNonNull(structureAcessDetailsList);
    }
}
