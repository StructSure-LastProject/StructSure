package fr.uge.structsure.repositories;

import fr.uge.structsure.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {}