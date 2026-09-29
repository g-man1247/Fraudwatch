package com.fraudwatch.model;

import com.fraudwatch.model.enums.Outcome;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "flagged_transactions")
@Data
@NoArgsConstructor
public class FlaggedTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "transaction_id", nullable = false)
    private Transaction transaction;

    @ManyToOne(optional = false)
    @JoinColumn(name = "rule_id", nullable = false)
    private Rule rule;

    @Column(nullable = false)
    private LocalDateTime flaggedAt;
    
    @Enumerated(EnumType.STRING)
    private Outcome reviewStatus; // null if pending review
}
