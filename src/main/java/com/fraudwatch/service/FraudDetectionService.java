package com.fraudwatch.service;

import com.fraudwatch.model.Rule;
import com.fraudwatch.model.Transaction;
import com.fraudwatch.model.enums.RuleType;
import com.fraudwatch.repository.RuleRepository;
import com.fraudwatch.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FraudDetectionService {
    private final RuleRepository ruleRepository;
    private final TransactionRepository transactionRepository;

    public List<Rule> evaluateTransaction(Transaction transaction) {
        List<Rule> activeRules = ruleRepository.findByEnabledTrue();
        List<Rule> triggeredRules = new ArrayList<>();

        for (Rule rule : activeRules) {
            if (rule.getType() == RuleType.LARGE_AMOUNT) {
                if (transaction.getAmount().compareTo(rule.getThresholdAmount()) > 0) {
                    triggeredRules.add(rule);
                }
            } else if (rule.getType() == RuleType.HIGH_FREQUENCY) {
                LocalDateTime start = transaction.getTimestamp().minusMinutes(rule.getTimeWindowMinutes());
                LocalDateTime end = transaction.getTimestamp();
                long recentCount = transactionRepository.countBySenderAndTimestampBetween(
                        transaction.getSender(), start, end);
                
                if (recentCount >= rule.getTransactionCount()) {
                    triggeredRules.add(rule);
                }
            }
        }
        return triggeredRules;
    }
}
