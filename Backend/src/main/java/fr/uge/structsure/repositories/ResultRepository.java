package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Result;
import fr.uge.structsure.entities.Structure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {
}
