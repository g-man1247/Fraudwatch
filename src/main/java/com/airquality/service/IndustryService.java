package com.airquality.service;

import com.airquality.entity.Industry;
import com.airquality.repository.IndustryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for Industry operations.
 * Contains business logic for creating, updating, and managing industries.
 */
@Service
public class IndustryService {

    @Autowired
    private IndustryRepository industryRepository;

    // ─── Create ─────────────────────────────────────────────────

    /**
     * Save a new industry to the database.
     *
     * @param industry the industry object from the request body
     * @return the saved industry with its generated ID
     */
    public Industry createIndustry(Industry industry) {
        return industryRepository.save(industry);
    }

    // ─── Read ────────────────────────────────────────────────────

    /**
     * Retrieve all industries.
     *
     * @return list of all registered industries
     */
    public List<Industry> getAllIndustries() {
        return industryRepository.findAll();
    }

    /**
     * Retrieve a single industry by its ID.
     *
     * @param id the industry ID
     * @return Optional containing the industry if found
     */
    public Optional<Industry> getIndustryById(Long id) {
        return industryRepository.findById(id);
    }

    // ─── Update ──────────────────────────────────────────────────

    /**
     * Update an existing industry's details.
     *
     * @param id      the ID of the industry to update
     * @param updated the new data from the request body
     * @return the updated industry, or null if not found
     */
    public Industry updateIndustry(Long id, Industry updated) {
        Optional<Industry> existing = industryRepository.findById(id);
        if (existing.isPresent()) {
            Industry industry = existing.get();
            industry.setIndustryName(updated.getIndustryName());
            industry.setLocation(updated.getLocation());
            industry.setContactPerson(updated.getContactPerson());
            industry.setContactEmail(updated.getContactEmail());
            industry.setComplianceStatus(updated.getComplianceStatus());
            return industryRepository.save(industry);
        }
        return null;
    }

    // ─── Delete ──────────────────────────────────────────────────

    /**
     * Delete an industry by ID.
     *
     * @param id the industry ID to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteIndustry(Long id) {
        if (industryRepository.existsById(id)) {
            industryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    // ─── Compliance Update ───────────────────────────────────────

    /**
     * Update the compliance status of an industry.
     * Called internally when a violation is detected.
     *
     * @param industryId the ID of the industry
     * @param status     the new compliance status (e.g. "VIOLATION")
     */
    public void updateComplianceStatus(Long industryId, String status) {
        Optional<Industry> opt = industryRepository.findById(industryId);
        opt.ifPresent(industry -> {
            industry.setComplianceStatus(status);
            industryRepository.save(industry);
        });
    }
}
