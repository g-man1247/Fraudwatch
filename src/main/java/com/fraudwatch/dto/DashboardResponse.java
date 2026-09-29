package com.fraudwatch.dto;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class DashboardResponse {
    private long totalTransactions;
    private long completedTransactions;
    private long flaggedTransactions;
    private long blockedTransactions;
    private long pendingReviews;
    private BigDecimal totalMoneyIn;
    private BigDecimal totalMoneyOut;
}
