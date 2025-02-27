package fr.uge.structsure.dto.sensors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;

import java.util.Objects;

/**
 * Represents the dto for the request of adding the position to the sensor in a plan
 */
@JsonSerialize
public record SensorPositionRequestDTO(Long structureId, Long planId, String controlChip, String measureChip, Integer x, Integer y) {

    /**
     * Validates the required fields of the sensor position request.
     *
     * @throws TraitementException If:
     *         <ul>
     *           <li>Any required field (structureId, planId, controlChip, measureChip, x, y) is null ({@code MISSING_FIELDS}).</li>
     *           <li>The control chip tag is empty or exceeds 32 characters ({@code SENSOR_CHIP_TAGS_EXCEED_LIMIT}).</li>
     *           <li>The measure chip tag is empty or exceeds 32 characters ({@code SENSOR_CHIP_TAGS_EXCEED_LIMIT}).</li>
     *         </ul>
     */
    public void checkFields() throws TraitementException {
        if (Objects.isNull(structureId) || Objects.isNull(planId) || Objects.isNull(controlChip) || Objects.isNull(measureChip)
        || Objects.isNull(x) || Objects.isNull(y)) {
            throw new TraitementException(Error.MISSING_FIELDS);
        }
        if (controlChip.isEmpty() || controlChip.length() > 32) {
            throw new TraitementException(Error.SENSOR_CHIP_TAGS_EXCEED_LIMIT);
        }
        if (measureChip.isEmpty() || measureChip.length() > 32) {
            throw new TraitementException(Error.SENSOR_CHIP_TAGS_EXCEED_LIMIT);
        }
    }
}
