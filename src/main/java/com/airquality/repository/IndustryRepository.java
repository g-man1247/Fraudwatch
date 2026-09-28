package com.airquality.repository;

import com.airquality.entity.Industry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Industry entity.
 * Provides standard CRUD operations via JpaRepository.
 */
@Repository
public interface IndustryRepository extends JpaRepository<Industry, Long> {
    // Standard JpaRepository methods cover all required CRUD operations.
    // Custom query methods can be added here in the future if needed.
}
