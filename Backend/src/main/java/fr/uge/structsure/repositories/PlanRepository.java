package com.example.springbootapi.repositories;

import com.example.springbootapi.entities.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanRepository extends JpaRepository<Plan, Long> {}