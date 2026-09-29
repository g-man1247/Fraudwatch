package com.fraudwatch.repository;

import com.fraudwatch.model.ReviewOutcome;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewOutcomeRepository extends JpaRepository<ReviewOutcome, Long> {
}
