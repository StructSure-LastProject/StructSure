package fr.uge.structsure.dto.sensors;

import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;

/**
 * Archive sensor request DTO
 * @param controlChip The control chip
 * @param measureChip The measure chip
 * @param isArchive Want to archive or not
 */
public record ArchiveSensorRequestDTO(String controlChip, String measureChip, boolean isArchive) {
    /**
     * Check fields
     * @throws TraitementException Thrown custom exceptions
     */
    public void checkFields() throws TraitementException {
        if (controlChip == null || measureChip == null || controlChip.isEmpty() || measureChip.isEmpty()) throw new TraitementException(Error.SENSOR_CHIP_TAGS_IS_EMPTY);
        if (controlChip.length() > 32 || measureChip.length() > 32) throw new TraitementException(Error.SENSOR_NAME_EXCEED_LIMIT);
    }
}
