package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Plan;
import fr.uge.structsure.entities.Structure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Objects;

/**
 * The repository of the plan entity
 */
public interface PlanRepository extends JpaRepository<Plan, Long> {

    /**
     * Counts the number of plan in the structure
     * @param structure the structure
     * @return the number
     */
    long countByStructure(Structure structure);

    /**
     * Find the list of the plans in the structure
     * @param structure the structure
     * @return the list of plans
     */
    List<Plan> findByStructure(Structure structure);

}