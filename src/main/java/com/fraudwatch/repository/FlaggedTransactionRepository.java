package com.fraudwatch.repository;

import com.fraudwatch.model.FlaggedTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FlaggedTransactionRepository extends JpaRepository<FlaggedTransaction, Long> {
    List<FlaggedTransaction> findByTransactionId(Long transactionId);
}
