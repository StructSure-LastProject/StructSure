package fr.uge.structsure.dto.structure;

import fr.uge.structsure.utils.StructureStateEnum;

import java.util.Objects;

/**
 * This record represents the response format of the get all Structures endpoint
 * @param id Id of the structure
 * @param name Name of the structure
 * @param numberOfSensors Number of sensors
 * @param numberOfPlans Number of plans
 * @param state The state of the structure
 */
public record AllStructureResponseDTO(long id, String name, int numberOfSensors, int numberOfPlans, StructureStateEnum state, boolean archived) {
    public AllStructureResponseDTO {
        Objects.requireNonNull(name);
        Objects.requireNonNull(state);
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