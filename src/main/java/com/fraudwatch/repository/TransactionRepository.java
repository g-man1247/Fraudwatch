package com.fraudwatch.repository;

import com.fraudwatch.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    long countBySenderAndTimestampBetween(String sender, LocalDateTime start, LocalDateTime end);
}
