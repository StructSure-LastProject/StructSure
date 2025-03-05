package fr.uge.structsure.dto.structure;

/**
 * The response to archive or restore a structure
 * @param id The structure id
 * @param name The structure name
 * @param archived Whether is archived or not
 */
public record ArchiveRestoreStructureResponseDto(long id, String name, String archived) {
}
