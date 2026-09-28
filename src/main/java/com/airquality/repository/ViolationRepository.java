package com.airquality.repository;

import com.airquality.entity.Violation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Violation entity.
 * Provides standard CRUD plus custom queries by industry.
 */
@Repository
public interface ViolationRepository extends JpaRepository<Violation, Long> {

    /**
     * Retrieve all violations associated with a specific industry.
     *
     * @param industryId the ID of the industry
     * @return list of violations for that industry
     */
    List<Violation> findByIndustryId(Long industryId);
}
