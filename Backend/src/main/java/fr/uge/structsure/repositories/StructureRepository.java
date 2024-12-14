package com.example.springbootapi.repositories;

import com.example.springbootapi.entities.Structure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StructureRepository extends JpaRepository<Structure, Long> {
    Structure findById(int id);
}