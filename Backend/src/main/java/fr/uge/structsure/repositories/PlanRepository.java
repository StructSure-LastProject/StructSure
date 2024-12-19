package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Plan;
import fr.uge.structsure.entities.Structure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Objects;

public interface PlanRepository extends JpaRepository<Plan, Long> {
    List<Plan> findAllByStructureId(Long structureId);
    int countByStructureId(Long structureId);

}