package com.airquality.controller;

import com.airquality.entity.Violation;
import com.airquality.service.ViolationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Violation operations.
 *
 * Base path: /api/violations
 *
 * Endpoints:
 *   POST /api/violations                        → Manually create a violation
 *   GET  /api/violations                        → Get all violations
 *   GET  /api/violations/{id}                   → Get violation by ID
 *   GET  /api/violations/industry/{industryId}  → Get violations by industry
 *
 * Note: Violations are also created automatically by AirQualityService
 * when a reading exceeds the configured pollutant threshold.
 */
@RestController
@RequestMapping("/api/violations")
public class ViolationController {

    @Autowired
    private ViolationService violationService;

    // ─── POST /api/violations ────────────────────────────────────

    /**
     * Manually create a violation record.
     *
     * Request body example:
     * {
     *   "industry": { "id": 1 },
     *   "station": { "id": 1 },
     *   "pollutant": "PM2.5",
     *   "measuredValue": 82.5,
     *   "permissibleValue": 60.0,
     *   "severity": "MEDIUM",
     *   "violationDate": "2026-09-27T10:00:00",
     *   "description": "PM2.5 exceeded threshold"
     * }
     *
     * @param violation the violation data from request body
     * @return 201 Created with saved violation
     */
    @PostMapping
    public ResponseEntity<Violation> createViolation(@RequestBody Violation violation) {
        Violation saved = violationService.createViolation(violation);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ─── GET /api/violations ─────────────────────────────────────

    /**
     * Retrieve all violation records.
     *
     * @return 200 OK with list of all violations
     */
    @GetMapping
    public ResponseEntity<List<Violation>> getAllViolations() {
        List<Violation> violations = violationService.getAllViolations();
        return ResponseEntity.ok(violations);
    }

    // ─── GET /api/violations/{id} ────────────────────────────────

    /**
     * Retrieve a single violation by its ID.
     *
     * @param id violation ID
     * @return 200 OK with violation, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Violation> getViolationById(@PathVariable Long id) {
        Optional<Violation> violation = violationService.getViolationById(id);
        return violation
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─── GET /api/violations/industry/{industryId} ───────────────

    /**
     * Retrieve all violations associated with a specific industry.
     *
     * @param industryId the industry ID
     * @return 200 OK with list of violations for that industry
     */
    @GetMapping("/industry/{industryId}")
    public ResponseEntity<List<Violation>> getViolationsByIndustry(@PathVariable Long industryId) {
        List<Violation> violations = violationService.getViolationsByIndustry(industryId);
        return ResponseEntity.ok(violations);
    }
}
