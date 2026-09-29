package com.fraudwatch.controller;

import com.fraudwatch.dto.ReviewRequest;
import com.fraudwatch.model.FlaggedTransaction;
import com.fraudwatch.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flagged-transactions")
@RequiredArgsConstructor
public class FlaggedTransactionController {
    private final ReviewService reviewService;

    @GetMapping
    public List<FlaggedTransaction> getAllFlaggedTransactions() {
        return reviewService.getAllFlaggedTransactions();
    }

    @GetMapping("/{id}")
    public FlaggedTransaction getFlaggedTransaction(@PathVariable Long id) {
        return reviewService.getFlaggedTransaction(id);
    }

    @PutMapping("/{id}/review")
    public void reviewTransaction(@PathVariable Long id, @Valid @RequestBody ReviewRequest request) {
        reviewService.reviewTransaction(id, request.getOutcome(), request.getReviewer(), request.getComments());
    }
}
