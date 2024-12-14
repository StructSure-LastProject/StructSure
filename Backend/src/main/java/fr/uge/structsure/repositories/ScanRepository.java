package com.example.springbootapi.repositories;

import com.example.springbootapi.entities.Scan;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ScanRepository extends JpaRepository<Scan, Long> {}