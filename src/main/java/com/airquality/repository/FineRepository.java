package com.airquality.repository;

import com.airquality.entity.Fine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Fine entity.
 * Provides standard CRUD plus custom queries by industry and payment status.
 */
@Repository
public interface FineRepository extends JpaRepository<Fine, Long> {

    /**
     * Retrieve all fines issued to a specific industry.
     *
     * @param industryId the ID of the industry
     * @return list of fines for that industry
     */
    List<Fine> findByIndustryId(Long industryId);

    /**
     * Retrieve fines filtered by their payment status.
     *
     * @param paymentStatus "PENDING" or "PAID"
     * @return list of fines matching the given status
     */
    List<Fine> findByPaymentStatus(String paymentStatus);
}
