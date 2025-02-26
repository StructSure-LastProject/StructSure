package fr.uge.structsure.dto.plan;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import fr.uge.structsure.entities.Plan;

@JsonSerialize
public record PlanDTO(
    long id,
    String name,
    String imageUrl,
    String section
) {
    /**
     * Alias to create a new DTO from a Plan object
     * @param plan the plan to get the data from
     */
    public PlanDTO(Plan plan) {
        this(plan.getId(), plan.getName(), plan.getImageUrl(), plan.getSection());
    }
}
