package com.airquality.service;

import com.airquality.entity.AirQualityReading;
import com.airquality.entity.Industry;
import com.airquality.entity.MonitoringStation;
import com.airquality.entity.Violation;
import com.airquality.entity.Fine;
import com.airquality.repository.AirQualityRepository;
import com.airquality.repository.IndustryRepository;
import com.airquality.repository.MonitoringStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Core service for processing air quality readings.
 *
 * Main flow:
 *   1. Receive a reading (with stationId, industryId, pollutant values)
 *   2. Resolve station and industry objects from the DB
 *   3. Check PM2.5 against the configured demo threshold (60.0)
 *   4. Set violationDetected flag
 *   5. Set timestamp
 *   6. Save the reading
 *   7. If violation detected → create Violation and Fine records
 *   8. Update industry compliance status
 *
 * The 60.0 threshold is a configurable project demo value.
 * Additional pollutant checks can be added in checkViolations().
 */
@Service
public class AirQualityService {

    // ─── Demo / configurable threshold for PM2.5 ────────────────
    private static final double PM25_THRESHOLD = 60.0;

    @Autowired
    private AirQualityRepository airQualityRepository;

    @Autowired
    private MonitoringStationRepository stationRepository;

    @Autowired
    private IndustryRepository industryRepository;

    @Autowired
    private ViolationService violationService;

    @Autowired
    private FineService fineService;

    // ─── Process & Save a Reading ────────────────────────────────

    /**
     * Main method to process an incoming air quality reading.
     *
     * The request body contains stationId and industryId as nested objects
     * (only ID is needed). This method resolves them to full entities,
     * checks for violations, saves everything, and returns the saved reading.
     *
     * @param reading the reading from the API request body
     * @return the saved AirQualityReading with generated ID
     */
    public AirQualityReading processAndSaveReading(AirQualityReading reading) {

        // 1. Resolve MonitoringStation from DB using provided station ID
        if (reading.getStation() != null && reading.getStation().getId() != null) {
            Optional<MonitoringStation> station = stationRepository.findById(reading.getStation().getId());
            station.ifPresent(reading::setStation);
        }

        // 2. Resolve Industry from DB using provided industry ID
        if (reading.getIndustry() != null && reading.getIndustry().getId() != null) {
            Optional<Industry> industry = industryRepository.findById(reading.getIndustry().getId());
            industry.ifPresent(reading::setIndustry);
        }

        // 3. Set the current timestamp
        reading.setTimestamp(LocalDateTime.now());

        // 4. Check pollutants and set violationDetected flag
        boolean isViolation = checkViolations(reading);
        reading.setViolationDetected(isViolation);

        // 5. Save the reading to the database
        AirQualityReading savedReading = airQualityRepository.save(reading);

        // 6. If a violation was detected, create Violation and Fine records
        if (isViolation) {
            handleViolationWorkflow(savedReading);
        }

        return savedReading;
    }

    // ─── Violation Detection Logic ───────────────────────────────

    /**
     * Checks pollutant values against configured thresholds.
     *
     * Currently only PM2.5 is checked (demo threshold: 60.0).
     * More pollutant checks can be added here later.
     *
     * @param reading the reading to evaluate
     * @return true if any pollutant exceeds its threshold
     */
    private boolean checkViolations(AirQualityReading reading) {
        // Check PM2.5 against the demo threshold
        if (reading.getPm25() != null && reading.getPm25() > PM25_THRESHOLD) {
            return true;
        }
        // Future pollutant checks can be added below:
        // e.g. PM10, NO2, SO2, CO, O3 with their own thresholds
        return false;
    }

    // ─── Violation & Fine Workflow ───────────────────────────────

    /**
     * When a violation is detected:
     *   - Create and save a Violation record
     *   - Calculate the fine amount based on exceedance level
     *   - Create and save a Fine record
     *   - Update the industry compliance status to "VIOLATION"
     *
     * @param reading the saved reading that triggered the violation
     */
    private void handleViolationWorkflow(AirQualityReading reading) {

        // Create a Violation record for PM2.5 exceedance
        Violation violation = new Violation();
        violation.setIndustry(reading.getIndustry());
        violation.setStation(reading.getStation());
        violation.setPollutant("PM2.5");
        violation.setMeasuredValue(reading.getPm25());
        violation.setPermissibleValue(PM25_THRESHOLD);
        violation.setViolationDate(LocalDateTime.now());
        violation.setDescription(
                "PM2.5 reading of " + reading.getPm25() +
                " µg/m³ exceeded the demo threshold of " + PM25_THRESHOLD + " µg/m³."
        );

        // Determine severity based on how much PM2.5 exceeds the threshold
        double exceedance = reading.getPm25() - PM25_THRESHOLD;
        if (exceedance <= 20.0) {
            violation.setSeverity("LOW");
        } else if (exceedance <= 50.0) {
            violation.setSeverity("MEDIUM");
        } else {
            violation.setSeverity("HIGH");
        }

        // Save the Violation
        Violation savedViolation = violationService.createViolation(violation);

        // Calculate fine amount:
        // Base fine + proportional amount based on exceedance
        double fineAmount = calculateFineAmount(exceedance, violation.getSeverity());

        // Create and save the Fine record
        Fine fine = new Fine();
        fine.setViolation(savedViolation);
        fine.setIndustry(reading.getIndustry());
        fine.setFineAmount(fineAmount);
        fine.setPaymentStatus("PENDING");
        fineService.createFine(fine);

        // Update the industry's compliance status to VIOLATION
        if (reading.getIndustry() != null) {
            reading.getIndustry().setComplianceStatus("VIOLATION");
            industryRepository.save(reading.getIndustry());
        }
    }

    /**
     * Calculate the monetary fine based on exceedance amount and severity.
     *
     * Fine formula (demo):
     *   LOW    → base 5,000 + (exceedance × 100)
     *   MEDIUM → base 15,000 + (exceedance × 200)
     *   HIGH   → base 30,000 + (exceedance × 500)
     *
     * @param exceedance amount by which PM2.5 exceeded the threshold
     * @param severity   "LOW", "MEDIUM", or "HIGH"
     * @return calculated fine amount
     */
    private double calculateFineAmount(double exceedance, String severity) {
        return switch (severity) {
            case "MEDIUM" -> 15000.0 + (exceedance * 200);
            case "HIGH"   -> 30000.0 + (exceedance * 500);
            default       -> 5000.0  + (exceedance * 100);  // LOW
        };
    }

    // ─── Retrieve Readings ───────────────────────────────────────

    /**
     * Retrieve all readings recorded by a specific monitoring station.
     *
     * @param stationId the station ID
     * @return list of readings from that station
     */
    public List<AirQualityReading> getReadingsByStation(Long stationId) {
        return airQualityRepository.findByStationId(stationId);
    }

    /**
     * Retrieve all air quality readings (across all stations).
     *
     * @return list of all readings
     */
    public List<AirQualityReading> getAllReadings() {
        return airQualityRepository.findAll();
    }
}
