package com.airquality.service;

import com.airquality.entity.Fine;
import com.airquality.repository.FineRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service layer for Fine management.
 * Handles creating fines, retrieving fines, and updating payment status.
 */
@Service
public class FineService {

    @Autowired
    private FineRepository fineRepository;

    // ─── Create ─────────────────────────────────────────────────

    /**
     * Save a new fine record to the database.
     * Called automatically by AirQualityService, or manually via the API.
     *
     * @param fine the fine object to save
     * @return the saved fine with generated ID
     */
    public Fine createFine(Fine fine) {
        return fineRepository.save(fine);
    }

    // ─── Read ────────────────────────────────────────────────────

    /**
     * Retrieve all fine records.
     *
     * @return list of all fines
     */
    public List<Fine> getAllFines() {
        return fineRepository.findAll();
    }

    /**
     * Retrieve a single fine by its ID.
     *
     * @param id fine ID
     * @return Optional containing the fine if found
     */
    public Optional<Fine> getFineById(Long id) {
        return fineRepository.findById(id);
    }

    /**
     * Retrieve all fines issued to a specific industry.
     *
     * @param industryId the industry ID
     * @return list of fines for that industry
     */
    public List<Fine> getFinesByIndustry(Long industryId) {
        return fineRepository.findByIndustryId(industryId);
    }

    /**
     * Retrieve fines filtered by payment status.
     *
     * @param status "PENDING" or "PAID"
     * @return list of matching fines
     */
    public List<Fine> getFinesByStatus(String status) {
        return fineRepository.findByPaymentStatus(status);
    }

    // ─── Update Payment Status ───────────────────────────────────

    /**
     * Mark a fine as PAID or update its payment status.
     *
     * When marked as PAID, the paymentDate is automatically
     * set to the current date and time.
     *
     * @param id     the fine ID to update
     * @param status the new payment status ("PAID" or "PENDING")
     * @return the updated Fine, or null if not found
     */
    public Fine updatePaymentStatus(Long id, String status) {
        Optional<Fine> existing = fineRepository.findById(id);
        if (existing.isPresent()) {
            Fine fine = existing.get();
            fine.setPaymentStatus(status);

            // Record the payment date when status changes to PAID
            if ("PAID".equalsIgnoreCase(status)) {
                fine.setPaymentDate(LocalDateTime.now());
            } else {
                // If reverting from PAID to PENDING, clear the payment date
                fine.setPaymentDate(null);
            }

            return fineRepository.save(fine);
        }
        return null;
    }
}
