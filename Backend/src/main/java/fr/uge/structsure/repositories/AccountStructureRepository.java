package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Account;
import fr.uge.structsure.entities.AccountStructure;
import fr.uge.structsure.entities.Structure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Account structure repository for CRUD operations
 */
public interface AccountStructureRepository extends JpaRepository<AccountStructure, Long> {

    /**
     * Find account structure object by account and structure
     * @param account The account object
     * @param structure The structure object
     * @return Optional<AccountStructure> The optional of account structure object
     */
    Optional<AccountStructure> findByAccountAndStructure(Account account, Structure structure);
}
