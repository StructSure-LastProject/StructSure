package fr.uge.structsure.dto.structure;

import fr.uge.structsure.entities.State;

import java.util.Objects;

/**
 * This record represents the response format of the get all Structures endpoint
 * @param id Id of the structure
 * @param name Name of the structure
 * @param numberOfSensors Number of sensors
 * @param numberOfPlans Number of plans
 * @param state The state of the structure
 * @param archived
 */
public record AllStructureResponseDTO(long id, String name, long numberOfSensors, long numberOfPlans, State state, boolean archived) {

    /**
     * Creates a new AllStructureResponseDTO from a state as an ordinal
     * instead of an enum instance.<br>
     * This convenience method is intended to be used for the criteria
     * query only. Use the standard constructor otherwise.
     * @param id The ID of the structure
     * @param name Name of the structure
     * @param numberOfSensors Number of sensors
     * @param numberOfPlans Number of plans
     * @param state The ordinal of the state of the structure
     * @param archived whether the structure is archived or not
     */
    public AllStructureResponseDTO(long id, String name, long numberOfSensors, long numberOfPlans, Integer state, boolean archived) {
        this(id, name, numberOfSensors, numberOfPlans, State.values()[state], archived);
    }

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