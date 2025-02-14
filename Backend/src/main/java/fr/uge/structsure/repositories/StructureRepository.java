package fr.uge.structsure.repositories;

import fr.uge.structsure.dto.structure.AllStructureResponseDTO;
import fr.uge.structsure.entities.Structure;
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


    @Query("""
        SELECT new fr.uge.structsure.dto.structure.AllStructureResponseDTO(
        s.id,
        s.name,
        COALESCE(COUNT(DISTINCT sensor.sensorId), 0),
        COALESCE(COUNT(DISTINCT plan.id), 0),
        CASE
             WHEN COUNT(DISTINCT sensor.sensorId) = 0 THEN 'UNKNOWN'
             WHEN SUM(CASE WHEN result.state = fr.uge.structsure.entities.State.DEFECTIVE THEN 1 ELSE 0 END) > 0 THEN 'DEFECTIVE'
             WHEN SUM(CASE WHEN result.state = fr.uge.structsure.entities.State.NOK THEN 1 ELSE 0 END) > 0 THEN 'NOK'
             ELSE 'OK'
         END,
         s.archived
        )
        FROM Structure s,
        Sensor sensor,
        Result result,
        Plan plan
        GROUP BY s.id
    """)
    List<AllStructureResponseDTO> findStructuresWithState();
}