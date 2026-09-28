package com.airquality.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a recorded pollution violation.
 *
 * A violation is created when an air quality reading
 * exceeds the configured permissible limit for a pollutant.
 */
@Entity
@Table(name = "violation")
public class Violation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Industry responsible for the violation.
     * Foreign key: industry_id
     */
    @ManyToOne
    @JoinColumn(name = "industry_id")
    private Industry industry;

    /**
     * Monitoring station that detected the violation.
     * Foreign key: station_id
     */
    @ManyToOne
    @JoinColumn(name = "station_id")
    private MonitoringStation station;

    /**
     * Name of the pollutant that exceeded the limit.
     * e.g. "PM2.5", "PM10", "NO2"
     */
    @Column(name = "pollutant", nullable = false)
    private String pollutant;

    /** The actual measured value recorded during the violation */
    @Column(name = "measured_value", nullable = false)
    private Double measuredValue;

    /** The configured permissible threshold for this pollutant */
    @Column(name = "permissible_value", nullable = false)
    private Double permissibleValue;

    /**
     * Violation severity level.
     * Possible values: LOW, MEDIUM, HIGH
     */
    @Column(name = "severity")
    private String severity;

    /** Date and time when the violation was detected */
    @Column(name = "violation_date")
    private LocalDateTime violationDate;

    /** Additional details or notes about the violation */
    @Column(name = "description")
    private String description;

    // ─── Constructors ───────────────────────────────────────────

    public Violation() {
    }

    // ─── Getters & Setters ──────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Industry getIndustry() { return industry; }
    public void setIndustry(Industry industry) { this.industry = industry; }

    public MonitoringStation getStation() { return station; }
    public void setStation(MonitoringStation station) { this.station = station; }

    public String getPollutant() { return pollutant; }
    public void setPollutant(String pollutant) { this.pollutant = pollutant; }

    public Double getMeasuredValue() { return measuredValue; }
    public void setMeasuredValue(Double measuredValue) { this.measuredValue = measuredValue; }

    public Double getPermissibleValue() { return permissibleValue; }
    public void setPermissibleValue(Double permissibleValue) { this.permissibleValue = permissibleValue; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public LocalDateTime getViolationDate() { return violationDate; }
    public void setViolationDate(LocalDateTime violationDate) { this.violationDate = violationDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @com.fasterxml.jackson.annotation.JsonSetter("stationId")
    public void setStationId(Long stationId) {
        if (stationId != null) {
            this.station = new MonitoringStation();
            this.station.setId(stationId);
        }
    }

    @com.fasterxml.jackson.annotation.JsonSetter("industryId")
    public void setIndustryId(Long industryId) {
        if (industryId != null) {
            this.industry = new Industry();
            this.industry.setId(industryId);
        }
    }
}
