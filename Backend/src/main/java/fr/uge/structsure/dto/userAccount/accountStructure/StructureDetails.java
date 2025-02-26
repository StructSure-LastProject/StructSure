package fr.uge.structsure.dto.userAccount.accountStructure;

import java.util.Objects;

public record StructureDetails(long structureId, String structureName, boolean hasAccess){
    public StructureDetails {
        Objects.requireNonNull(structureName);
    }
}
