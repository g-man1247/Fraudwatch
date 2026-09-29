package com.fraudwatch.service;

import com.fraudwatch.dto.TransactionRequest;
import com.fraudwatch.exception.ResourceNotFoundException;
import com.fraudwatch.model.FlaggedTransaction;
import com.fraudwatch.model.Rule;
import com.fraudwatch.model.Transaction;
import com.fraudwatch.model.enums.TransactionStatus;
import com.fraudwatch.repository.FlaggedTransactionRepository;
import com.fraudwatch.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final FraudDetectionService fraudDetectionService;
    private final FlaggedTransactionRepository flaggedTransactionRepository;
    private final AccountService accountService;

    @Transactional
    public Transaction processTransaction(TransactionRequest request) {
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setSender(request.getSender());
        transaction.setReceiver(request.getReceiver());
        transaction.setTimestamp(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.PENDING);
        transaction.setType(request.getType());
        
        transaction = transactionRepository.save(transaction);

        List<Rule> triggeredRules = fraudDetectionService.evaluateTransaction(transaction);

        if (triggeredRules.isEmpty()) {
            transaction.setStatus(TransactionStatus.COMPLETED);
            accountService.transfer(transaction.getSender(), transaction.getReceiver(), transaction.getAmount());
        } else {
            transaction.setStatus(TransactionStatus.FLAGGED);
            for (Rule rule : triggeredRules) {
                FlaggedTransaction ft = new FlaggedTransaction();
                ft.setTransaction(transaction);
                ft.setRule(rule);
                ft.setFlaggedAt(LocalDateTime.now());
                flaggedTransactionRepository.save(ft);
            }
        }
        return transactionRepository.save(transaction);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    public Transaction getTransaction(Long id) {
        return transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));
    }
}
