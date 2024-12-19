package fr.uge.structsure.dto.structure;

import java.util.Objects;

/**
 * This record represents the response format of the get all Structures endpoint
 * @param id Id of the structure
 * @param name Name of the structure
 * @param numberOfSensors Number of sensors
 * @param numberOfPlans Number of plans
 * @param url The url to access the structure
 */
public record AllStructureResponseDTO(long id, String name, int numberOfSensors, int numberOfPlans, String url) {
    public AllStructureResponseDTO {
        Objects.requireNonNull(name);
        Objects.requireNonNull(url);
        if (id <= 0) {
            throw new IllegalArgumentException("id must be greater than 0");
        }
        if (numberOfSensors < 0) {
            throw new IllegalArgumentException("numberOfSensors must be greater or equal 0");
        }
        if (numberOfPlans < 0) {
            throw new IllegalArgumentException("numberOfPlans must be greater or equal 0");
        }
    }
}