package fr.uge.structsure.dto.structure;

/**
 * The response to archive or restore a structure
 * @param id The structure id
 * @param name The structure name
 * @param archived Whether is archived or not
 */
public record ArchiveRestoreStructureResponseDTO(long id, String name, boolean archived) {
}
