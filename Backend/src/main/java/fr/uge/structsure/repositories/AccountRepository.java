package com.example.springbootapi.repositories;

import com.example.springbootapi.entities.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, String> {}