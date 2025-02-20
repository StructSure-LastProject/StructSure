package fr.uge.structsure.dto.userAccount.accountStructure;

/**
 * Record that represents the structure with the asked permission
 * @param structureId The structure id
 * @param hasAccess true or false the permission
 */
public record StructurePermission(long structureId, boolean hasAccess){}
