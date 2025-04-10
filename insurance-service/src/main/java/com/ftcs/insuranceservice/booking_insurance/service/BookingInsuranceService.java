package com.ftcs.insuranceservice.booking_insurance.service;

import com.ftcs.common.exception.BadRequestException;
import com.ftcs.insuranceservice.booking_insurance.model.BookingInsurance;
import com.ftcs.insuranceservice.booking_insurance.repository.BookingInsuranceRepository;
import com.ftcs.insuranceservice.booking_type.service.BookingTypeService;
import com.ftcs.insuranceservice.insurance_policy.model.InsurancePolicy;
import com.ftcs.insuranceservice.insurance_policy.service.InsurancePolicyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class BookingInsuranceService {

    private final BookingInsuranceRepository bookingInsuranceRepository;
    private final InsurancePolicyService insurancePolicyService;

    public void createBookingInsurance(Long bookingTypeId, Integer accountId, Double price, Long bookingId) {
        InsurancePolicy insurancePolicy = insurancePolicyService.getInsurancePolicyByBookingType(bookingTypeId);
        BookingInsurance bookingInsurance = BookingInsurance.builder()
                .bookingId(bookingId)
                .accountId(accountId)
                .policyId(insurancePolicy.getPolicyId())
                .calculatedPremium(price*insurancePolicy.getPremiumPercentage() / 100)
                .calculateCompensation(price*insurancePolicy.getCompensationPercentage() / 100)
                .premiumPercentage(insurancePolicy.getPremiumPercentage())
                .compensationPercentage(insurancePolicy.getCompensationPercentage())
                .status("ACTIVE")
                .build();
        bookingInsuranceRepository.save(bookingInsurance);
    }

    public BookingInsurance getBookingInsuranceById(Long id) {
        return bookingInsuranceRepository.findBookingInsuranceById(id)
                .orElseThrow(() -> new BadRequestException("BookingInsurance not found"));
    }

    public Page<BookingInsurance> getAllBookingInsurances(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookingInsuranceRepository.findAll(pageable);
    }

    public Page<BookingInsurance> getBookingInsurancesByAccountId(Integer accountId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return bookingInsuranceRepository.findAllByAccountId(accountId, pageable);
    }

    public BookingInsurance findByBookingId(Long bookingId) {
        return bookingInsuranceRepository.findByBookingId(bookingId)
                .orElseThrow(() -> new BadRequestException("BookingInsurance not found"));
    }


}
