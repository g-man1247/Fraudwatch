package com.airquality.repository;

import com.airquality.entity.MonitoringStation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for MonitoringStation entity.
 * Provides standard CRUD operations via JpaRepository.
 */
@Repository
public interface MonitoringStationRepository extends JpaRepository<MonitoringStation, Long> {

    /**
     * Find all monitoring stations associated with a specific industry.
     *
     * @param industryId the ID of the industry
     * @return list of stations belonging to that industry
     */
    List<MonitoringStation> findByIndustryId(Long industryId);
}
