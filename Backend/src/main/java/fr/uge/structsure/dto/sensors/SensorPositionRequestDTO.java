package fr.uge.structsure.dto.sensors;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.entities.State;
import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;
import fr.uge.structsure.utils.EnumValidatorFromString;
import fr.uge.structsure.utils.OrderEnum;

import java.util.Objects;

/**
 * Represents the dto for the request of adding the position to the sensor in a plan
 */
@JsonSerialize
public record SensorPositionRequestDTO(Long structureId, Long planId, String controlChip, String measureChip, Integer x, Integer y) {


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
