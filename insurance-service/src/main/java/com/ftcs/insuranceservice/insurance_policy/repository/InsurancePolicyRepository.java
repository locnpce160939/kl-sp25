package com.ftcs.insuranceservice.insurance_policy.repository;

import com.ftcs.insuranceservice.insurance_policy.model.InsurancePolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InsurancePolicyRepository extends JpaRepository<InsurancePolicy, Long> {
    Optional<InsurancePolicy> findInsurancePolicyByPolicyId(Long policyId);
    List<InsurancePolicy> findByBookingType(Long bookingType);
    Optional<InsurancePolicy> findInsurancePolicyByBookingType(Long bookingType);
}
