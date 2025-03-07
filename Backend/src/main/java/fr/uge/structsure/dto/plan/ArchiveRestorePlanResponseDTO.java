package fr.uge.structsure.dto.plan;

/**
 * The response to archive or restore a plan
 *  * @param id The plan id
 *  * @param name The structure name
 *  * @param archived Whether is archived or not
 */
public record ArchiveRestorePlanResponseDTO(long id, String name, boolean archived) {
}
