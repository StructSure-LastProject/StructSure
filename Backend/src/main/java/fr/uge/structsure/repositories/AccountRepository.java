package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Account;
import fr.uge.structsure.entities.Scan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {
  @Query("""
           SELECT a
           FROM Account a
           JOIN Scan s ON s.author.login = a.login
           WHERE s.id = :scanId
           """)
  Optional<Account> findAuthorByScan(@Param("scanId") Long scanId);

  Optional<Account> findByLoginAndEnabledTrue(String login);
}