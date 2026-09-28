package com.airquality.controller;

import com.airquality.entity.AirQualityReading;
import com.airquality.service.AirQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Air Quality Reading operations.
 *
 * Base path: /api/air-quality
 *
 * Handles:
 *   - Submitting a new reading (which triggers violation detection)
 *   - Retrieving readings by station
 *   - Retrieving all readings
 */
@RestController
@RequestMapping("/api/air-quality")
public class AirQualityController {

    @Autowired
    private AirQualityService airQualityService;

    // ─── POST /api/air-quality/readings ─────────────────────────

    /**
     * Submit a new air quality reading.
     *
     * The service automatically:
     *   1. Resolves station and industry from their IDs
     *   2. Checks PM2.5 against the demo threshold (60.0)
     *   3. Sets violationDetected flag
     *   4. Saves the reading
     *   5. Creates Violation and Fine if threshold exceeded
     *   6. Updates industry compliance status
     *
     * Request body example:
     * {
     *   "station": { "id": 1 },
     *   "industry": { "id": 1 },
     *   "pm25": 82.5,
     *   "pm10": 110.0,
     *   "co": 1.2,
     *   "co2": 420.0,
     *   "no2": 40.0,
     *   "so2": 20.0,
     *   "o3": 30.0,
     *   "temperature": 31.5,
     *   "humidity": 65.0
     * }
     *
     * @param reading the reading data from the request body
     * @return 200 OK with the saved reading JSON
     */
    @PostMapping("/readings")
    public ResponseEntity<AirQualityReading> submitReading(@RequestBody AirQualityReading reading) {
        AirQualityReading saved = airQualityService.processAndSaveReading(reading);
        return ResponseEntity.ok(saved);
    }

    // ─── GET /api/air-quality/readings/{stationId} ───────────────

    /**
     * Retrieve all readings from a specific monitoring station.
     *
     * @param stationId the station ID (path variable)
     * @return 200 OK with list of readings for that station
     */
    @GetMapping("/readings/{stationId}")
    public ResponseEntity<List<AirQualityReading>> getReadingsByStation(@PathVariable Long stationId) {
        List<AirQualityReading> readings = airQualityService.getReadingsByStation(stationId);
        return ResponseEntity.ok(readings);
    }

    // ─── GET /api/air-quality/readings ──────────────────────────

    /**
     * Retrieve all air quality readings from all stations.
     *
     * @return 200 OK with list of all readings
     */
    @GetMapping("/readings")
    public ResponseEntity<List<AirQualityReading>> getAllReadings() {
        List<AirQualityReading> readings = airQualityService.getAllReadings();
        return ResponseEntity.ok(readings);
    }
}
