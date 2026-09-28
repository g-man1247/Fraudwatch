package com.airquality.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Entity representing a monetary fine issued for a pollution violation.
 *
 * Each fine is linked to exactly one Violation and one Industry.
 * Payment is tracked via paymentStatus.
 */
@Entity
@Table(name = "fine")
public class Fine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * The specific violation this fine was issued for.
     * Foreign key: violation_id
     */
    @ManyToOne
    @JoinColumn(name = "violation_id", nullable = false)
    private Violation violation;

    /**
     * The industry that was fined.
     * Foreign key: industry_id
     */
    @ManyToOne
    @JoinColumn(name = "industry_id", nullable = false)
    private Industry industry;

    /** Amount of the fine in currency units */
    @Column(name = "fine_amount", nullable = false)
    private Double fineAmount;

    /**
     * Payment status of the fine.
     * Possible values: PENDING, PAID
     */
    @Column(name = "payment_status", nullable = false)
    private String paymentStatus;

    /**
     * Date the fine was paid (null if still PENDING).
     */
    @Column(name = "payment_date")
    private LocalDateTime paymentDate;

    // ─── Constructors ───────────────────────────────────────────

    public Fine() {
    }

    // ─── Getters & Setters ──────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Violation getViolation() { return violation; }
    public void setViolation(Violation violation) { this.violation = violation; }

    public Industry getIndustry() { return industry; }
    public void setIndustry(Industry industry) { this.industry = industry; }

    public Double getFineAmount() { return fineAmount; }
    public void setFineAmount(Double fineAmount) { this.fineAmount = fineAmount; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public LocalDateTime getPaymentDate() { return paymentDate; }
    public void setPaymentDate(LocalDateTime paymentDate) { this.paymentDate = paymentDate; }

    @com.fasterxml.jackson.annotation.JsonSetter("violationId")
    public void setViolationId(Long violationId) {
        if (violationId != null) {
            this.violation = new Violation();
            this.violation.setId(violationId);
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
