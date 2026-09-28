package com.airquality.controller;

import com.airquality.entity.Fine;
import com.airquality.service.FineService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Fine management operations.
 *
 * Base path: /api/fines
 *
 * Endpoints:
 *   POST   /api/fines                          → Manually create a fine
 *   GET    /api/fines                          → Get all fines
 *   GET    /api/fines/{id}                     → Get fine by ID
 *   GET    /api/fines/industry/{industryId}    → Get fines by industry
 *   GET    /api/fines/status/{status}          → Get fines by payment status
 *   PUT    /api/fines/{id}/pay                 → Mark fine as PAID
 *   PUT    /api/fines/{id}/status              → Update payment status
 *
 * Note: Fines are also created automatically by AirQualityService.
 */
@RestController
@RequestMapping("/api/fines")
public class FineController {

    @Autowired
    private FineService fineService;

    // ─── POST /api/fines ─────────────────────────────────────────

    /**
     * Manually create a fine record.
     *
     * Request body example:
     * {
     *   "violation": { "id": 1 },
     *   "industry": { "id": 1 },
     *   "fineAmount": 15000.00,
     *   "paymentStatus": "PENDING"
     * }
     *
     * @param fine the fine data from request body
     * @return 201 Created with saved fine
     */
    @PostMapping
    public ResponseEntity<Fine> createFine(@RequestBody Fine fine) {
        Fine saved = fineService.createFine(fine);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ─── GET /api/fines ──────────────────────────────────────────

    /**
     * Retrieve all fine records.
     *
     * @return 200 OK with list of all fines
     */
    @GetMapping
    public ResponseEntity<List<Fine>> getAllFines() {
        List<Fine> fines = fineService.getAllFines();
        return ResponseEntity.ok(fines);
    }

    // ─── GET /api/fines/{id} ─────────────────────────────────────

    /**
     * Retrieve a single fine by its ID.
     *
     * @param id fine ID
     * @return 200 OK with fine, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Fine> getFineById(@PathVariable Long id) {
        Optional<Fine> fine = fineService.getFineById(id);
        return fine
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─── GET /api/fines/industry/{industryId} ────────────────────

    /**
     * Retrieve all fines issued to a specific industry.
     *
     * @param industryId the industry ID
     * @return 200 OK with list of fines for that industry
     */
    @GetMapping("/industry/{industryId}")
    public ResponseEntity<List<Fine>> getFinesByIndustry(@PathVariable Long industryId) {
        List<Fine> fines = fineService.getFinesByIndustry(industryId);
        return ResponseEntity.ok(fines);
    }

    // ─── GET /api/fines/status/{status} ──────────────────────────

    /**
     * Retrieve fines filtered by payment status.
     *
     * @param status "PENDING" or "PAID"
     * @return 200 OK with list of matching fines
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Fine>> getFinesByStatus(@PathVariable String status) {
        List<Fine> fines = fineService.getFinesByStatus(status);
        return ResponseEntity.ok(fines);
    }

    // ─── PUT /api/fines/{id}/pay ─────────────────────────────────

    /**
     * Mark a fine as PAID.
     * Automatically records the payment date.
     *
     * @param id fine ID to mark as paid
     * @return 200 OK with updated fine, or 404 if not found
     */
    @PutMapping("/{id}/pay")
    public ResponseEntity<Fine> markAsPaid(@PathVariable Long id) {
        Fine updated = fineService.updatePaymentStatus(id, "PAID");
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }

    // ─── PUT /api/fines/{id}/status ──────────────────────────────

    /**
     * Update the payment status of a fine to any valid status.
     *
     * Request param: status=PENDING or status=PAID
     *
     * Example: PUT /api/fines/1/status?status=PAID
     *
     * @param id     the fine ID
     * @param status the new payment status
     * @return 200 OK with updated fine, or 404 if not found
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<Fine> updatePaymentStatus(@PathVariable Long id, @RequestParam String status) {
        Fine updated = fineService.updatePaymentStatus(id, status);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }
}
