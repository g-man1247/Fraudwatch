package com.airquality.entity;

import jakarta.persistence.*;

/**
 * Entity representing a physical Air Quality Monitoring Station
 * deployed at or near an industrial facility.
 */
@Entity
@Table(name = "monitoring_station")
public class MonitoringStation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Human-readable name or code for this station */
    @Column(name = "station_name", nullable = false)
    private String stationName;

    /** Geographic location or address of the station */
    @Column(name = "location")
    private String location;

    /**
     * The industry this station monitors.
     * Stored as a foreign key (industry_id) in the database.
     */
    @ManyToOne
    @JoinColumn(name = "industry_id")
    private Industry industry;

    // ─── Constructors ───────────────────────────────────────────

    public MonitoringStation() {
    }

    public MonitoringStation(String stationName, String location, Industry industry) {
        this.stationName = stationName;
        this.location = location;
        this.industry = industry;
    }

    // ─── Getters & Setters ──────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStationName() { return stationName; }
    public void setStationName(String stationName) { this.stationName = stationName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Industry getIndustry() { return industry; }
    public void setIndustry(Industry industry) { this.industry = industry; }

    @com.fasterxml.jackson.annotation.JsonSetter("industryId")
    public void setIndustryId(Long industryId) {
        if (industryId != null) {
            this.industry = new Industry();
            this.industry.setId(industryId);
        }
    }
}
