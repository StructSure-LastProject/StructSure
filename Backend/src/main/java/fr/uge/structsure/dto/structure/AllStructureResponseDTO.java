package fr.uge.structsure.dto.structure;

/**
 * This record represents the response format of the get all Structures endpoint
 * @param id Id of the structure
 * @param name Name of the structure
 * @param numberOfSensors Number of sensors
 * @param numberOfPlans Number of plans
 * @param url The url to access the structure
 */
public record AllStructureResponseDTO(long id, String name, int numberOfSensors, int numberOfPlans, String url) {

}