package fr.uge.structsure.repositories;

import fr.uge.structsure.dto.structure.AllStructureResponseDTO;
import fr.uge.structsure.entities.Structure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface StructureRepository extends JpaRepository<Structure, Long> {
    Optional<Structure> findByName(String name);

    Optional<Structure> findById(long id);

    @Query("""
        SELECT new fr.uge.structsure.dto.structure.AllStructureResponseDTO(
        s.id,
        s.name,
        COALESCE(COUNT(DISTINCT sensor.sensorId), 0), 
        COALESCE(COUNT(DISTINCT plan.id), 0),
        CASE 
             WHEN COUNT(DISTINCT sensor.sensorId) = 0 THEN 'UNKNOWN'
             WHEN SUM(CASE WHEN result.state = fr.uge.structsure.entities.State.DEFAULTER THEN 1 ELSE 0 END) > 0 THEN 'DEFAULTER'
             WHEN SUM(CASE WHEN result.state = fr.uge.structsure.entities.State.NOK THEN 1 ELSE 0 END) > 0 THEN 'NOK'
             ELSE 'OK' 
         END, 
         s.archived
        )
        FROM Structure s
        LEFT JOIN Sensor sensor ON sensor.structure = s 
        LEFT JOIN Result result ON result.sensor = sensor 
        LEFT JOIN Plan plan ON plan.structure = s 
        GROUP BY s.id
    """)
    List<AllStructureResponseDTO> findStructuresWithState();
}