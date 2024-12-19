package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Structure;
import fr.uge.structsure.utils.SortEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface StructureRepository extends JpaRepository<Structure, Long> {
  List<Structure> findByArchivedFalse();
}