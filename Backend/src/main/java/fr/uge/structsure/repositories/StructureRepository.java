package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Structure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface StructureRepository extends JpaRepository<Structure, Long> {
}