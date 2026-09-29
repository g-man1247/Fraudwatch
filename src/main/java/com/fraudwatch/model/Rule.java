package com.fraudwatch.model;

import com.fraudwatch.model.enums.RuleType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "rules")
@Data
@NoArgsConstructor
public class Rule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RuleType type;

    private boolean enabled;

    private BigDecimal thresholdAmount;

    private Integer transactionCount;

    private Integer timeWindowMinutes;

    private String description;
}
