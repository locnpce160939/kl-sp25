package com.ftcs.financeservice.weight_range.service;

import com.ftcs.common.exception.BadRequestException;
import com.ftcs.financeservice.distance_range.dto.DistanceRangeRequestDTO;
import com.ftcs.financeservice.distance_range.model.DistanceRange;
import com.ftcs.financeservice.distance_range.repository.DistanceRangeRepository;
import com.ftcs.financeservice.weight_range.dto.WeightRangeRequestDTO;
import com.ftcs.financeservice.weight_range.model.WeightRange;
import com.ftcs.financeservice.weight_range.repository.WeightRangeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class WeightRangeService {

    private final WeightRangeRepository weightRangeRepository;

    public Page<WeightRange> getAllWeightRanges(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return weightRangeRepository.findAll(pageable);
    }

    public WeightRange findByWeightRangeId(Integer weightRangeId) {
        return weightRangeRepository.findByWeightRangeId(weightRangeId)
                .orElseThrow(() -> new BadRequestException("No weight range found."));
    }

    public void createWeightRange(Integer accountId, WeightRangeRequestDTO requestDTO) {
        if (requestDTO.getMaxWeight() <= requestDTO.getMinWeight()) {
            throw new BadRequestException("Maximum weight (maxWeight) must be greater than minimum weight (minWeight).");
        }

        Double minWeight = requestDTO.getMinWeight();
        Double maxWeight = requestDTO.getMaxWeight();

        if (weightRangeRepository.existsByOverlappingRange(minWeight, maxWeight)) {
            throw new BadRequestException("The specified weight range overlaps with an existing range.");
        }

        WeightRange weightRange = WeightRange.builder()
                .minWeight(minWeight)
                .maxWeight(maxWeight)
                .createdDate(LocalDateTime.now())
                .updatedBy(accountId)
                .build();

        weightRangeRepository.save(weightRange);
    }

    public void updateWeightRange(Integer accountId, Integer weightRangeId, WeightRangeRequestDTO requestDTO) {
        if (requestDTO.getMaxWeight() <= requestDTO.getMinWeight()) {
            throw new BadRequestException("Maximum weight (maxWeight) must be greater than minimum weight (minWeight).");
        }

        Double newMinWeight = requestDTO.getMinWeight();
        Double newMaxWeight = requestDTO.getMaxWeight();

        WeightRange existingRange = findByWeightRangeId(weightRangeId);

        boolean hasOverlap = weightRangeRepository.existsByOverlappingRangeExcludingId(newMinWeight, newMaxWeight, weightRangeId);

        if (hasOverlap) {
            throw new BadRequestException("The specified weight range overlaps with an existing range.");
        }

        existingRange.setMinWeight(newMinWeight);
        existingRange.setMaxWeight(newMaxWeight);
        existingRange.setUpdatedBy(accountId);
        existingRange.setUpdatedDate(LocalDateTime.now());

        weightRangeRepository.save(existingRange);
    }
}

