package com.fraudwatch.service;

import com.fraudwatch.dto.DashboardResponse;
import com.fraudwatch.model.Transaction;
import com.fraudwatch.model.enums.TransactionStatus;
import com.fraudwatch.model.enums.TransactionType;
import com.fraudwatch.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {
    private final TransactionRepository transactionRepository;

    public DashboardResponse getSummary() {
        List<Transaction> all = transactionRepository.findAll();

        long total = all.size();
        long completed = all.stream().filter(t -> t.getStatus() == TransactionStatus.COMPLETED).count();
        long flagged = all.stream().filter(t -> t.getStatus() == TransactionStatus.FLAGGED).count();
        long blocked = all.stream().filter(t -> t.getStatus() == TransactionStatus.BLOCKED).count();

        BigDecimal totalIn = all.stream()
                .filter(t -> t.getType() == TransactionType.IN)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalOut = all.stream()
                .filter(t -> t.getType() == TransactionType.OUT)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return DashboardResponse.builder()
                .totalTransactions(total)
                .completedTransactions(completed)
                .flaggedTransactions(flagged)
                .blockedTransactions(blocked)
                .pendingReviews(flagged)
                .totalMoneyIn(totalIn)
                .totalMoneyOut(totalOut)
                .build();
    }
}
