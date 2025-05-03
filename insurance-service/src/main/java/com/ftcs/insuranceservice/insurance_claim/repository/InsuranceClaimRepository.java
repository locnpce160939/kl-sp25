package com.ftcs.insuranceservice.insurance_claim.repository;

import com.ftcs.insuranceservice.insurance_claim.constant.ClaimStatus;
import com.ftcs.insuranceservice.insurance_claim.model.InsuranceClaim;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface InsuranceClaimRepository extends JpaRepository<InsuranceClaim, Long> {
    Optional<InsuranceClaim> findInsuranceClaimById(Long id);
    Optional<InsuranceClaim> findInsuranceClaimByBookingId(Long id);
    Page<InsuranceClaim> findByClaimStatus(ClaimStatus status, Pageable pageable);
    Page<InsuranceClaim> findByClaimDateBetween(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
    Boolean existsByBookingId(Long bookingId);
}
