package fr.uge.structsure.repositories;

import fr.uge.structsure.dto.structure.AllStructureRequestDTO;
import fr.uge.structsure.dto.structure.AllStructureResponseDTO;
import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.utils.OrderEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * Repository for structures
 */
@Repository
public interface StructureRepository extends JpaRepository<Structure, Long> {
    /**
     * Will find a strucutre by its name
     * @param name the name of the structure
     * @return optional with the strucutre if there is a strucutre and optional empty if not
     */
    Optional<Structure> findByName(String name);

    /**
     * Will find a strucutre by its id
     * @param id the id of the structure
     * @return optional with the strucutre if there is a strucutre and optional empty if not
     */
    Optional<Structure> findById(long id);
}