package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Plan;
import fr.uge.structsure.entities.Structure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

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
     * Will find the plan by its id, and also that is present in the structure
     * @param planId the plan id
     * @param structure the structure
     * @return Optional<Plan> an optional with the plan or an optional empty if there is no plan
     */
    @Query("""
        SELECT plan
        FROM Plan plan
        WHERE plan.structure = :structure
        AND plan.id = :planId
    """)
    Optional<Plan> findByStructureAndPlanId(long planId, Structure structure);

    /**
     * Find the list of the plans in the structure
     * @param structure the structure
     * @return the list of plans
     */
    List<Plan> findByStructure(Structure structure);

    @Query("SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM Plan p WHERE p.imageUrl = :url")
    boolean planFileAlreadyExists(String url);
}