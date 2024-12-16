package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {}