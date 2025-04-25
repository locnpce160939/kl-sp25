package com.ftcs.financeservice.pricing.service;

import com.ftcs.common.exception.BadRequestException;
import com.ftcs.financeservice.distance_range.repository.DistanceRangeRepository;
import com.ftcs.financeservice.holiday_surcharge.model.HolidaySurcharge;
import com.ftcs.financeservice.pricing.dto.PricingRequestDTO;
import com.ftcs.financeservice.pricing.model.Pricing;
import com.ftcs.financeservice.pricing.repository.PricingRepository;
import com.ftcs.financeservice.weight_range.repository.WeightRangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PricingService {

    private final PricingRepository pricingRepository;
    private final WeightRangeRepository weightRangeRepository;
    private final DistanceRangeRepository distanceRangeRepository;

    public Page<Pricing> getAllPricing(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return pricingRepository.findAll(pageable);
    }

    public Pricing getPricingById(Integer pricingId) {
        return pricingRepository.findByPricingId(pricingId)
                .orElseThrow(() -> new BadRequestException("No pricing found with id: " + pricingId));
    }

    public void createPricing(Integer accountId, PricingRequestDTO requestDTO) {
        validateRangeIds(requestDTO);
        Pricing pricing = buildPricing(accountId, requestDTO);
        pricingRepository.save(pricing);
    }

    public void updatePricing(Integer accountId, Integer pricingId, PricingRequestDTO requestDTO) {
        validateRangeIds(requestDTO);
        Pricing existingPricing = getPricingById(pricingId);
        updatePricingDetails(existingPricing, accountId, requestDTO);
        pricingRepository.save(existingPricing);
    }

    public void deletePricing(Integer pricingId) {
        getPricingById(pricingId);
        pricingRepository.deleteById(pricingId);
    }

    private void validateRangeIds(PricingRequestDTO requestDTO) {
        if (!distanceRangeRepository.existsById(requestDTO.getDistanceRangeId())) {
            throw new BadRequestException("Invalid distanceRangeId provided");
        }
        if (!weightRangeRepository.existsById(requestDTO.getWeightRangeId())) {
            throw new BadRequestException("Invalid weightRangeId provided");
        }
    }

    private Pricing buildPricing(Integer accountId, PricingRequestDTO requestDTO) {
        return Pricing.builder()
                .distanceRangeId(requestDTO.getDistanceRangeId())
                .weightRangeId(requestDTO.getWeightRangeId())
                .basePrice(requestDTO.getBasePrice())
                .updatedBy(accountId)
                .createdDate(LocalDateTime.now())
                .build();
    }

    private void updatePricingDetails(Pricing pricing, Integer accountId, PricingRequestDTO requestDTO) {
        pricing.setDistanceRangeId(requestDTO.getDistanceRangeId());
        pricing.setWeightRangeId(requestDTO.getWeightRangeId());
        pricing.setBasePrice(requestDTO.getBasePrice());
        pricing.setUpdatedBy(accountId);
        pricing.setUpdatedDate(LocalDateTime.now());
    }
}
