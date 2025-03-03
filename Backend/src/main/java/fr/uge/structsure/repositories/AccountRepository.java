package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {

    /**
     * Find the acccount by login
     * @param login the login
     * @return optional with the login if there is and optional empty if there is no login
     */
    Optional<Account> findByLogin(String login);

    /**
     * Find the account by user id
     * @param id the user id
     * @return optional with the account if there is and optional empty if there is no user
     */
    Optional<Account> findById(Long id);

}
