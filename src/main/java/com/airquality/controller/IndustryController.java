package com.airquality.controller;

import com.airquality.entity.Industry;
import com.airquality.service.IndustryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for Industry CRUD operations.
 *
 * Base path: /api/industries
 *
 * Endpoints:
 *   POST   /api/industries         → Create industry
 *   GET    /api/industries         → Get all industries
 *   GET    /api/industries/{id}    → Get industry by ID
 *   PUT    /api/industries/{id}    → Update industry
 *   DELETE /api/industries/{id}    → Delete industry
 */
@RestController
@RequestMapping("/api/industries")
public class IndustryController {

    @Autowired
    private IndustryService industryService;

    // ─── POST /api/industries ────────────────────────────────────

    /**
     * Create a new industry.
     *
     * Request body:
     * {
     *   "industryName": "ABC Manufacturing",
     *   "location": "Coimbatore",
     *   "contactPerson": "Manager",
     *   "contactEmail": "manager@example.com",
     *   "complianceStatus": "COMPLIANT"
     * }
     *
     * @param industry the industry data from request body
     * @return 201 Created with the saved industry
     */
    @PostMapping
    public ResponseEntity<Industry> createIndustry(@RequestBody Industry industry) {
        Industry saved = industryService.createIndustry(industry);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ─── GET /api/industries ─────────────────────────────────────

    /**
     * Retrieve all registered industries.
     *
     * @return 200 OK with list of all industries
     */
    @GetMapping
    public ResponseEntity<List<Industry>> getAllIndustries() {
        List<Industry> industries = industryService.getAllIndustries();
        return ResponseEntity.ok(industries);
    }

    // ─── GET /api/industries/{id} ────────────────────────────────

    /**
     * Retrieve a single industry by its ID.
     *
     * @param id the industry ID
     * @return 200 OK with the industry, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Industry> getIndustryById(@PathVariable Long id) {
        Optional<Industry> industry = industryService.getIndustryById(id);
        return industry
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─── PUT /api/industries/{id} ────────────────────────────────

    /**
     * Update an existing industry.
     *
     * @param id      the ID of the industry to update
     * @param updated the updated industry data from request body
     * @return 200 OK with updated industry, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<Industry> updateIndustry(@PathVariable Long id, @RequestBody Industry updated) {
        Industry result = industryService.updateIndustry(id, updated);
        if (result != null) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }

    // ─── DELETE /api/industries/{id} ─────────────────────────────

    /**
     * Delete an industry by ID.
     *
     * @param id the industry ID to delete
     * @return 200 OK with success message, or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteIndustry(@PathVariable Long id) {
        boolean deleted = industryService.deleteIndustry(id);
        if (deleted) {
            return ResponseEntity.ok("Industry with ID " + id + " deleted successfully.");
        }
        return ResponseEntity.notFound().build();
    }
}
