package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.utils.OrderEnum;
import fr.uge.structsure.utils.SortStructuresByEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface StructureRepository extends JpaRepository<Structure, Long> {
    Optional<Structure> findByName(String name);

    Optional<Structure> findById(long id);

    @Query("SELECT * FROM Structure " +
            "WHERE st")
    List<?> getStructuresFilteredSortedState(String searchByName,
                                                     SortStructuresByEnum sortStructuresByEnum,
                                                     OrderEnum orderEnum);
}