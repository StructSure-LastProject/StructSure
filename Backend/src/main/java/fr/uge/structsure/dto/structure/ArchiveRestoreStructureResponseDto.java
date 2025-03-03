package fr.uge.structsure.dto.structure;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * The response to archive or restore a structure
 * @param id The structure id
 * @param name The structure name
 * @param archived Whether is archived or not
 * @param updatedAt When the structure was changed
 */
public record ArchiveRestoreStructureResponseDto(long id, String name, String archived, String updatedAt) {
}
