package fr.uge.structsure.dto.sensors;

import fr.uge.structsure.exceptions.Error;
import fr.uge.structsure.exceptions.TraitementException;

import java.util.Objects;

/**
 * Dto for the request to get the all logs with pagination
 * @param search the search string (optional)
 * @param page the index of the page to get
 */
public record LogsRequestDTO(String search, Integer page) {

    /**
     * Checks if the limit and offset have valid positive values
     * @throws TraitementException if one field is missing or have an
     *     incorrect value
     */
    public void checkFields() throws TraitementException {
        if (Objects.isNull(page)) {
            throw new TraitementException(Error.MISSING_FIELDS);
        }
        if (page < 0) {
            throw new TraitementException(Error.INCORRECT_FIELD_VALUE);
        }
    }
}
