package com.fraudwatch.service;

import com.fraudwatch.exception.InvalidStateException;
import com.fraudwatch.exception.ResourceNotFoundException;
import com.fraudwatch.model.FlaggedTransaction;
import com.fraudwatch.model.ReviewOutcome;
import com.fraudwatch.model.Transaction;
import com.fraudwatch.model.enums.Outcome;
import com.fraudwatch.model.enums.TransactionStatus;
import com.fraudwatch.repository.FlaggedTransactionRepository;
import com.fraudwatch.repository.ReviewOutcomeRepository;
import com.fraudwatch.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final TransactionRepository transactionRepository;
    private final FlaggedTransactionRepository flaggedTransactionRepository;
    private final ReviewOutcomeRepository reviewOutcomeRepository;
    private final AccountService accountService;

    @Transactional
    public void reviewTransaction(Long transactionId, Outcome outcome, String reviewer, String comments) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (transaction.getStatus() != TransactionStatus.FLAGGED) {
            throw new InvalidStateException("Transaction is not in FLAGGED state");
        }

        List<FlaggedTransaction> flags = flaggedTransactionRepository.findByTransactionId(transactionId);
        for (FlaggedTransaction flag : flags) {
            flag.setReviewStatus(outcome);
            flaggedTransactionRepository.save(flag);
        }

        ReviewOutcome review = new ReviewOutcome();
        review.setTransaction(transaction);
        review.setOutcome(outcome);
        review.setReviewedAt(LocalDateTime.now());
        review.setReviewer(reviewer);
        review.setComments(comments);
        reviewOutcomeRepository.save(review);

        if (outcome == Outcome.APPROVED) {
            transaction.setStatus(TransactionStatus.COMPLETED);
            accountService.transfer(transaction.getSender(), transaction.getReceiver(), transaction.getAmount());
        } else {
            transaction.setStatus(TransactionStatus.BLOCKED);
        }
        transactionRepository.save(transaction);
    }
    
    public List<FlaggedTransaction> getAllFlaggedTransactions() {
        return flaggedTransactionRepository.findAll();
    }
    
    public FlaggedTransaction getFlaggedTransaction(Long id) {
        return flaggedTransactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Flagged transaction not found"));
    }
}
