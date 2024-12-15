package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Structure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StructureRepository extends JpaRepository<Structure, Long> {
    Structure findById(int id);
}