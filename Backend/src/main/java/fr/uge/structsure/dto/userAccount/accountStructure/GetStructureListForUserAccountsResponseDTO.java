package fr.uge.structsure.dto.userAccount.accountStructure;

import java.util.List;
import java.util.Objects;

public record GetStructureListForUserAccountsResponseDTO(List<StructureDetails> structureDetailsList) {
    public GetStructureListForUserAccountsResponseDTO {
        Objects.requireNonNull(structureDetailsList);
    }
}
