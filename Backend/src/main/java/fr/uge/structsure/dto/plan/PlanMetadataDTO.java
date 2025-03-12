package fr.uge.structsure.dto.plan;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.entities.Plan;
import fr.uge.structsure.utils.DiffMaker;

/**
 * DTO to map the attributes of a plan add/edit request (except the
 * file that is in a multipart object)
 * @param section the section of the plan to set
 * @param name the name of the plan
 */
@JsonSerialize
public record PlanMetadataDTO(String section, String name) {

    /**
     * Creates a list of updated fields ready to be logged.
     * @param plan the initial data
     * @param imageUpdated true if a new image have been sent
     * @return the changed values
     */
    public String logDiff(Plan plan, boolean imageUpdated) {
        return new DiffMaker()
            .add("Nom", plan.getName(), name)
            .add("Section", plan.getSection(), section)
            .add(imageUpdated, () -> "Image mise à jour")
            .toString();
    }
}
