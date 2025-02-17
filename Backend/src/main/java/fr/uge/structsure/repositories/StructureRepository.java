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

    /**
     * Returns the list of structures sorted by sortTypeEnum in asc order and for each one its state, number of sensors,
     * number of plans and if it's archived
     * @return List<AllStructureResponseDTO> list of the structures
     */
    @Query("""
        SELECT new fr.uge.structsure.dto.structure.AllStructureResponseDTO(
        s.id,
        s.name,
        COUNT(DISTINCT sensor.sensorId.measureChip),
        COUNT(DISTINCT plan.id),
        CASE
             WHEN COUNT(DISTINCT sensor.sensorId) = 0 THEN "UNKNOWN"
             WHEN SUM(CASE WHEN result.state = fr.uge.structsure.entities.State.DEFECTIVE THEN 1 ELSE 0 END) > 0 THEN  "DEFECTIVE"
             WHEN SUM(CASE WHEN result.state = fr.uge.structsure.entities.State.NOK THEN 1 ELSE 0 END) > 0 THEN "NOK"
             ELSE "OK"
         END,
         s.archived
        )
        FROM Structure s
        LEFT JOIN Sensor sensor ON sensor.structure = s
        LEFT JOIN Result result ON result.sensor = sensor
        LEFT JOIN Plan plan ON plan.structure = s
        WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :containsName, '%'))
        GROUP BY s.id
        ORDER BY :sortTypeEnum ASC
    """)
    List<AllStructureResponseDTO> findAllStructuresWithStateAsc(AllStructureRequestDTO.SortTypeEnum sortTypeEnum, String containsName);

    /**
     * Returns the list of structures sorted by sortTypeEnum in desc order and for each one its state, number of sensors,
     * number of plans and if it's archived
     * @return List<AllStructureResponseDTO> list of the structures
     */
    @Query("""
        SELECT new fr.uge.structsure.dto.structure.AllStructureResponseDTO(
        s.id,
        s.name,
        COUNT(DISTINCT sensor.sensorId.measureChip),
        COUNT(DISTINCT plan.id),
        CASE
             WHEN COUNT(DISTINCT sensor.sensorId) = 0 THEN "UNKNOWN"
             WHEN SUM(CASE WHEN result.state = fr.uge.structsure.entities.State.DEFECTIVE THEN 1 ELSE 0 END) > 0 THEN  "DEFECTIVE"
             WHEN SUM(CASE WHEN result.state = fr.uge.structsure.entities.State.NOK THEN 1 ELSE 0 END) > 0 THEN "NOK"
             ELSE "OK"
         END,
         s.archived
        )
        FROM Structure s
        LEFT JOIN Sensor sensor ON sensor.structure = s
        LEFT JOIN Result result ON result.sensor = sensor
        LEFT JOIN Plan plan ON plan.structure = s
        WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :containsName, '%'))
        GROUP BY s.id
        ORDER BY :sortTypeEnum DESC
    """)
    List<AllStructureResponseDTO> findAllStructuresWithStateDesc(AllStructureRequestDTO.SortTypeEnum sortTypeEnum, String containsName);
}