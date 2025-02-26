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
     * Will find the plan by its id, and also that is present in the structure
     * @param structure the structure
     * @param planId the plan id
     * @return Optional<Plan> an optional with the plan or an optional empty if there is no plan
     */
    Optional<Plan> findByStructureAndId(Structure structure, long planId);

    /**
     * Find the list of the plans in the structure
     * @param structure the structure
     * @return the list of plans
     */
    List<Plan> findByStructure(Structure structure);

    /**
     * Finds all the plan linked to the given structure that are not
     * archived.
     * @param structure the structure to get plans from
     * @return all the not archived structure's plan
     */
    List<Plan> findByStructureAndArchivedFalse(Structure structure);

    @Query("SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END FROM Plan p WHERE p.imageUrl = :url")
    boolean planFileAlreadyExists(String url);
}