package com.ftcs.financeservice.holiday_surcharge.service;

import com.ftcs.common.exception.BadRequestException;
import com.ftcs.financeservice.holiday_surcharge.dto.HolidaySurchargeRequestDTO;
import com.ftcs.financeservice.holiday_surcharge.model.HolidaySurcharge;
import com.ftcs.financeservice.holiday_surcharge.repository.HolidaySurchargeRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class HolidaySurchargeService {
    private final HolidaySurchargeRepository holidaySurchargeRepository;

    public Page<HolidaySurcharge> getAllHolidaySurcharges(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return holidaySurchargeRepository.findAll(pageable);
    }

    public HolidaySurcharge getHolidaySurchargeById(Integer holidaySurchargeId) {
        return holidaySurchargeRepository.findByHolidaySurchargeId(holidaySurchargeId).
                orElseThrow(() -> new BadRequestException("Holiday surcharge not found"));
    }

    public void createHolidaySurcharge(Integer accountId, HolidaySurchargeRequestDTO requestDTO) {
        validateHolidayPeriod(requestDTO.getStartDate(), requestDTO.getEndDate());
        HolidaySurcharge holidaySurcharge = HolidaySurcharge.builder()
                .holidayName(requestDTO.getHolidayName())
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .surchargePercentage(requestDTO.getSurchargePercentage())
                .createdDate(LocalDateTime.now())
                .updatedBy(accountId)
                .build();
        holidaySurchargeRepository.save(holidaySurcharge);
    }

    public void updateHolidaySurcharge(Integer accountId, Integer holidaySurchargeId, HolidaySurchargeRequestDTO requestDTO) {
        HolidaySurcharge holidaySurcharge = getHolidaySurchargeById(holidaySurchargeId);
        boolean datesChanged = !holidaySurcharge.getStartDate().equals(requestDTO.getStartDate()) ||
                !holidaySurcharge.getEndDate().equals(requestDTO.getEndDate());
        if (datesChanged) {
            validateHolidayPeriod(requestDTO.getStartDate(), requestDTO.getEndDate());
        }
        holidaySurcharge.setHolidayName(requestDTO.getHolidayName());
        holidaySurcharge.setStartDate(requestDTO.getStartDate());
        holidaySurcharge.setEndDate(requestDTO.getEndDate());
        holidaySurcharge.setSurchargePercentage(requestDTO.getSurchargePercentage());
        holidaySurcharge.setUpdatedBy(accountId);
        holidaySurcharge.setUpdatedDate(LocalDateTime.now());

        holidaySurchargeRepository.save(holidaySurcharge);
    }

    public void deleteHolidaySurcharge(Integer holidaySurchargeId) {
        holidaySurchargeRepository.deleteById(holidaySurchargeId);
    }

    private void validateHolidayPeriod(LocalDateTime startDate, LocalDateTime endDate) {
        if (holidaySurchargeRepository.existsByStartDateAndEndDate(startDate, endDate)) {
            throw new BadRequestException("A holiday surcharge already exists for the given date range.");
        }
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date must be before or equal to the end date.");
        }
    }
}
