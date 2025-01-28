package com.ftcs.financeservice.pricing.repository;

import com.ftcs.financeservice.pricing.model.Pricing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PricingRepository extends JpaRepository<Pricing, Integer> {
    Optional<Pricing> findByPricingId(Integer pricingId);
}
