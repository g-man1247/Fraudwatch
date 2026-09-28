package com.airquality.repository;

import com.airquality.entity.AirQualityReading;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for AirQualityReading entity.
 * Provides standard CRUD plus custom queries by station and industry.
 */
@Repository
public interface AirQualityRepository extends JpaRepository<AirQualityReading, Long> {

    /**
     * Retrieve all air quality readings for a specific monitoring station.
     *
     * @param stationId the ID of the monitoring station
     * @return list of readings from that station
     */
    List<AirQualityReading> findByStationId(Long stationId);

    /**
     * Retrieve all air quality readings linked to a specific industry.
     *
     * @param industryId the ID of the industry
     * @return list of readings for that industry
     */
    List<AirQualityReading> findByIndustryId(Long industryId);
}
