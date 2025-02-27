package fr.uge.structsure.dto.userAccount.accountStructure;

import java.util.Objects;

/**
 * Structure details
 * @param structureId The structure id
 * @param structureName The structure name
 * @param hasAccess true for has access or false
 */
public record StructureAcessDetails(long structureId, String structureName, boolean hasAccess){
    /**
     * Constructor
     * @param structureId The structure id
     * @param structureName The structure name
     * @param hasAccess true for has access or false
     */
    public StructureAcessDetails {
        Objects.requireNonNull(structureName);
    }
}
