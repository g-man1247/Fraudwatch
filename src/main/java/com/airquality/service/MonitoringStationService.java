package com.airquality.service;

import com.airquality.entity.Industry;
import com.airquality.entity.MonitoringStation;
import com.airquality.repository.IndustryRepository;
import com.airquality.repository.MonitoringStationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Service layer for MonitoringStation operations.
 * Handles CRUD operations and station-to-industry association.
 */
@Service
public class MonitoringStationService {

    @Autowired
    private MonitoringStationRepository stationRepository;

    @Autowired
    private IndustryRepository industryRepository;

    // ─── Create ─────────────────────────────────────────────────

    /**
     * Create a new monitoring station.
     * If an industryId is included in the station's industry field,
     * the station is linked to that industry.
     *
     * @param station the station data from the request body
     * @return saved MonitoringStation with generated ID
     */
    public MonitoringStation createStation(MonitoringStation station) {
        return stationRepository.save(station);
    }

    // ─── Read ────────────────────────────────────────────────────

    /**
     * Retrieve all monitoring stations.
     *
     * @return list of all stations
     */
    public List<MonitoringStation> getAllStations() {
        return stationRepository.findAll();
    }

    /**
     * Retrieve a monitoring station by ID.
     *
     * @param id station ID
     * @return Optional containing the station if found
     */
    public Optional<MonitoringStation> getStationById(Long id) {
        return stationRepository.findById(id);
    }

    /**
     * Retrieve all stations belonging to a specific industry.
     *
     * @param industryId the industry ID
     * @return list of stations for that industry
     */
    public List<MonitoringStation> getStationsByIndustry(Long industryId) {
        return stationRepository.findByIndustryId(industryId);
    }

    // ─── Update ──────────────────────────────────────────────────

    /**
     * Update an existing monitoring station.
     *
     * @param id      the station ID to update
     * @param updated the new station data
     * @return the updated station, or null if not found
     */
    public MonitoringStation updateStation(Long id, MonitoringStation updated) {
        Optional<MonitoringStation> existing = stationRepository.findById(id);
        if (existing.isPresent()) {
            MonitoringStation station = existing.get();
            station.setStationName(updated.getStationName());
            station.setLocation(updated.getLocation());

            // Update industry link if provided
            if (updated.getIndustry() != null && updated.getIndustry().getId() != null) {
                Optional<Industry> industry = industryRepository.findById(updated.getIndustry().getId());
                industry.ifPresent(station::setIndustry);
            }
            return stationRepository.save(station);
        }
        return null;
    }

    // ─── Delete ──────────────────────────────────────────────────

    /**
     * Delete a monitoring station by ID.
     *
     * @param id the station ID
     * @return true if deleted, false if not found
     */
    public boolean deleteStation(Long id) {
        if (stationRepository.existsById(id)) {
            stationRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
