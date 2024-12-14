package com.example.springbootapi.repositories;

import com.example.springbootapi.entities.Result;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResultRepository extends JpaRepository<Result, Long> {}