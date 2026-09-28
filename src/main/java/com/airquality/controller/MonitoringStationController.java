package com.airquality.controller;

import com.airquality.entity.MonitoringStation;
import com.airquality.service.MonitoringStationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for MonitoringStation CRUD operations.
 *
 * Base path: /api/stations
 *
 * Endpoints:
 *   POST   /api/stations                       → Create station
 *   GET    /api/stations                       → Get all stations
 *   GET    /api/stations/{id}                  → Get station by ID
 *   GET    /api/stations/industry/{industryId} → Get stations by industry
 *   PUT    /api/stations/{id}                  → Update station
 *   DELETE /api/stations/{id}                  → Delete station
 */
@RestController
@RequestMapping("/api/stations")
public class MonitoringStationController {

    @Autowired
    private MonitoringStationService stationService;

    // ─── POST /api/stations ──────────────────────────────────────

    /**
     * Create a new monitoring station.
     *
     * Request body example:
     * {
     *   "stationName": "Station Alpha",
     *   "location": "Coimbatore North Zone",
     *   "industry": { "id": 1 }
     * }
     *
     * @param station the station data from request body
     * @return 201 Created with saved station
     */
    @PostMapping
    public ResponseEntity<MonitoringStation> createStation(@RequestBody MonitoringStation station) {
        MonitoringStation saved = stationService.createStation(station);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // ─── GET /api/stations ───────────────────────────────────────

    /**
     * Retrieve all monitoring stations.
     *
     * @return 200 OK with list of all stations
     */
    @GetMapping
    public ResponseEntity<List<MonitoringStation>> getAllStations() {
        List<MonitoringStation> stations = stationService.getAllStations();
        return ResponseEntity.ok(stations);
    }

    // ─── GET /api/stations/{id} ──────────────────────────────────

    /**
     * Retrieve a monitoring station by its ID.
     *
     * @param id the station ID
     * @return 200 OK with station, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<MonitoringStation> getStationById(@PathVariable Long id) {
        Optional<MonitoringStation> station = stationService.getStationById(id);
        return station
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ─── GET /api/stations/industry/{industryId} ─────────────────

    /**
     * Retrieve all monitoring stations associated with a specific industry.
     *
     * @param industryId the industry ID
     * @return 200 OK with list of stations for that industry
     */
    @GetMapping("/industry/{industryId}")
    public ResponseEntity<List<MonitoringStation>> getStationsByIndustry(@PathVariable Long industryId) {
        List<MonitoringStation> stations = stationService.getStationsByIndustry(industryId);
        return ResponseEntity.ok(stations);
    }

    // ─── PUT /api/stations/{id} ──────────────────────────────────

    /**
     * Update an existing monitoring station.
     *
     * @param id      the station ID to update
     * @param updated the new station data from request body
     * @return 200 OK with updated station, or 404 if not found
     */
    @PutMapping("/{id}")
    public ResponseEntity<MonitoringStation> updateStation(@PathVariable Long id, @RequestBody MonitoringStation updated) {
        MonitoringStation result = stationService.updateStation(id, updated);
        if (result != null) {
            return ResponseEntity.ok(result);
        }
        return ResponseEntity.notFound().build();
    }

    // ─── DELETE /api/stations/{id} ───────────────────────────────

    /**
     * Delete a monitoring station by ID.
     *
     * @param id the station ID to delete
     * @return 200 OK with message, or 404 if not found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStation(@PathVariable Long id) {
        boolean deleted = stationService.deleteStation(id);
        if (deleted) {
            return ResponseEntity.ok("Station with ID " + id + " deleted successfully.");
        }
        return ResponseEntity.notFound().build();
    }
}
