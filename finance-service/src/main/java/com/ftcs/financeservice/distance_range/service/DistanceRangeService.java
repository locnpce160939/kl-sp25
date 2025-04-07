package com.ftcs.financeservice.distance_range.service;

import com.ftcs.common.exception.BadRequestException;
import com.ftcs.financeservice.distance_range.dto.DistanceRangeRequestDTO;
import com.ftcs.financeservice.distance_range.model.DistanceRange;
import com.ftcs.financeservice.distance_range.repository.DistanceRangeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class DistanceRangeService {
    private final DistanceRangeRepository distanceRangeRepository;

    public Page<DistanceRange> getAllDistanceRanges(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return distanceRangeRepository.findAll(pageable);
    }

    public DistanceRange findByDistanceId(Integer distanceId) {
        return distanceRangeRepository.findByDistanceRangeId(distanceId)
                .orElseThrow(() -> new BadRequestException("No distance range found."));
    }

    public void createDistanceRange(Integer accountId, DistanceRangeRequestDTO requestDTO) {
        if (requestDTO.getMaxKm() <= requestDTO.getMinKm()) {
            throw new BadRequestException("Maximum distance (maxKm) must be greater than minimum distance (minKm).");
        }

        boolean hasOverlap = distanceRangeRepository.existsByOverlappingRange(
                requestDTO.getMinKm(), requestDTO.getMaxKm()
        );

        if (hasOverlap) {
            throw new BadRequestException("The specified distance range overlaps with an existing range.");
        }

        DistanceRange distanceRange = DistanceRange.builder()
                .minKm(requestDTO.getMinKm())
                .maxKm(requestDTO.getMaxKm())
                .createdDate(LocalDateTime.now())
                .updatedBy(accountId)
                .build();
        distanceRangeRepository.save(distanceRange);
    }

    public void updateDistanceRange(Integer accountId, Integer distanceRangeId, DistanceRangeRequestDTO requestDTO) {
        if (requestDTO.getMaxKm() <= requestDTO.getMinKm()) {
            throw new BadRequestException("Maximum distance (maxKm) must be greater than minimum distance (minKm).");
        }
        Double newMinKm = requestDTO.getMinKm();
        Double newMaxKm = requestDTO.getMaxKm();
        DistanceRange existingRange = findByDistanceId(distanceRangeId);
        boolean hasOverlap = distanceRangeRepository.existsByOverlappingRangeExcludingId(newMinKm, newMaxKm, distanceRangeId);
        if (hasOverlap) {
            throw new BadRequestException("The specified distance range overlaps with an existing range.");
        }
        existingRange.setMinKm(newMinKm);
        existingRange.setMaxKm(newMaxKm);
        existingRange.setUpdatedBy(accountId);
        existingRange.setUpdatedDate(LocalDateTime.now());
        distanceRangeRepository.save(existingRange);
    }

}
