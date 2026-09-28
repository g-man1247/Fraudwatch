package com.airquality.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a single air quality measurement snapshot
 * taken by a monitoring station.
 *
 * All pollutant values are stored as Double (null means "not measured").
 * The violationDetected flag is set automatically by the service layer
 * based on configurable thresholds.
 */
@Entity
@Table(name = "air_quality_reading")
public class AirQualityReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The monitoring station that produced this reading.
     * Foreign key: station_id
     */
    @ManyToOne
    @JoinColumn(name = "station_id")
    private MonitoringStation station;

    /**
     * The industry associated with this reading.
     * Foreign key: industry_id
     */
    @ManyToOne
    @JoinColumn(name = "industry_id")
    private Industry industry;

    // ─── Pollutant Measurements ─────────────────────────────────

    /** PM2.5 – Fine particulate matter (µg/m³) */
    @Column(name = "pm25")
    private Double pm25;

    /** PM10 – Coarse particulate matter (µg/m³) */
    @Column(name = "pm10")
    private Double pm10;

    /** CO – Carbon Monoxide (mg/m³) */
    @Column(name = "co")
    private Double co;

    /** CO2 – Carbon Dioxide (ppm) */
    @Column(name = "co2")
    private Double co2;

    /** NO2 – Nitrogen Dioxide (µg/m³) */
    @Column(name = "no2")
    private Double no2;

    /** SO2 – Sulfur Dioxide (µg/m³) */
    @Column(name = "so2")
    private Double so2;

    /** O3 – Ozone (µg/m³) */
    @Column(name = "o3")
    private Double o3;

    /** Ambient temperature (°C) */
    @Column(name = "temperature")
    private Double temperature;

    /** Relative humidity (%) */
    @Column(name = "humidity")
    private Double humidity;

    // ─── Computed Fields ────────────────────────────────────────

    /**
     * True if any pollutant exceeded its configured threshold.
     * Set automatically by AirQualityService before saving.
     */
    @Column(name = "violation_detected")
    private Boolean violationDetected;

    /** Timestamp when the reading was recorded */
    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    // ─── Constructors ───────────────────────────────────────────

    public AirQualityReading() {
    }

    // ─── Getters & Setters ──────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public MonitoringStation getStation() { return station; }
    public void setStation(MonitoringStation station) { this.station = station; }

    public Industry getIndustry() { return industry; }
    public void setIndustry(Industry industry) { this.industry = industry; }

    public Double getPm25() { return pm25; }
    public void setPm25(Double pm25) { this.pm25 = pm25; }

    public Double getPm10() { return pm10; }
    public void setPm10(Double pm10) { this.pm10 = pm10; }

    public Double getCo() { return co; }
    public void setCo(Double co) { this.co = co; }

    public Double getCo2() { return co2; }
    public void setCo2(Double co2) { this.co2 = co2; }

    public Double getNo2() { return no2; }
    public void setNo2(Double no2) { this.no2 = no2; }

    public Double getSo2() { return so2; }
    public void setSo2(Double so2) { this.so2 = so2; }

    public Double getO3() { return o3; }
    public void setO3(Double o3) { this.o3 = o3; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getHumidity() { return humidity; }
    public void setHumidity(Double humidity) { this.humidity = humidity; }

    public Boolean getViolationDetected() { return violationDetected; }
    public void setViolationDetected(Boolean violationDetected) { this.violationDetected = violationDetected; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

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
