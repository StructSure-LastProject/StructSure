package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.utils.SortEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface StructureRepository extends JpaRepository<Structure, Long> {
    Optional<Structure> findByName(String name);
}