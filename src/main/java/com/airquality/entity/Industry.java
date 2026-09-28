package com.airquality.entity;

import jakarta.persistence.*;

/**
 * Entity representing an Industrial facility/company
 * that is monitored for air quality compliance.
 */
@Entity
@Table(name = "industry")
public class Industry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Name of the industrial facility */
    @Column(name = "industry_name", nullable = false)
    private String industryName;

    /** Physical address or area of the industry */
    @Column(name = "location", nullable = false)
    private String location;

    /** Primary contact person at the industry */
    @Column(name = "contact_person")
    private String contactPerson;

    /** Contact email for the industry representative */
    @Column(name = "contact_email")
    private String contactEmail;

    /**
     * Current compliance status of the industry.
     * Possible values: COMPLIANT, VIOLATION, UNDER_REVIEW
     */
    @Column(name = "compliance_status")
    private String complianceStatus;

    // ─── Constructors ───────────────────────────────────────────

    public Industry() {
    }

    public Industry(String industryName, String location, String contactPerson,
                    String contactEmail, String complianceStatus) {
        this.industryName = industryName;
        this.location = location;
        this.contactPerson = contactPerson;
        this.contactEmail = contactEmail;
        this.complianceStatus = complianceStatus;
    }

    // ─── Getters & Setters ──────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getIndustryName() { return industryName; }
    public void setIndustryName(String industryName) { this.industryName = industryName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getContactPerson() { return contactPerson; }
    public void setContactPerson(String contactPerson) { this.contactPerson = contactPerson; }

    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }

    public String getComplianceStatus() { return complianceStatus; }
    public void setComplianceStatus(String complianceStatus) { this.complianceStatus = complianceStatus; }
}
