package com.airquality.service;

import com.airquality.entity.Violation;
import com.airquality.repository.ViolationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Violation operations.
 * Handles creating, retrieving, and managing violation records.
 */
@Service
public class ViolationService {

    @Autowired
    private ViolationRepository violationRepository;

    // ─── Create ─────────────────────────────────────────────────

    /**
     * Save a new violation record to the database.
     * Called automatically by AirQualityService when a violation is detected.
     * Can also be called manually via the API.
     *
     * @param violation the violation object to save
     * @return the saved violation with generated ID
     */
    public Violation createViolation(Violation violation) {
        return violationRepository.save(violation);
    }

    // ─── Read ────────────────────────────────────────────────────

    /**
     * Retrieve all violation records.
     *
     * @return list of all violations
     */
    public List<Violation> getAllViolations() {
        return violationRepository.findAll();
    }

    /**
     * Retrieve a violation by its ID.
     *
     * @param id violation ID
     * @return Optional containing the violation if found
     */
    public Optional<Violation> getViolationById(Long id) {
        return violationRepository.findById(id);
    }

    /**
     * Retrieve all violations associated with a specific industry.
     *
     * @param industryId the industry ID
     * @return list of violations for that industry
     */
    public List<Violation> getViolationsByIndustry(Long industryId) {
        return violationRepository.findByIndustryId(industryId);
    }
}
