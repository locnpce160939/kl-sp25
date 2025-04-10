package com.ftcs.insuranceservice.insurance_claim.service;

import com.ftcs.accountservice.account.serivce.AccountService;
import com.ftcs.authservice.features.account.Account;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.insuranceservice.booking_insurance.model.BookingInsurance;
import com.ftcs.insuranceservice.booking_insurance.service.BookingInsuranceService;
import com.ftcs.insuranceservice.insurance_claim.constant.ClaimStatus;
import com.ftcs.insuranceservice.insurance_claim.dto.InsuranceClaimRequestDTO;
import com.ftcs.insuranceservice.insurance_claim.model.InsuranceClaim;
import com.ftcs.insuranceservice.insurance_claim.repository.InsuranceClaimRepository;
import com.ftcs.insuranceservice.insurance_policy.model.InsurancePolicy;
import com.ftcs.insuranceservice.insurance_policy.service.InsurancePolicyService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class InsuranceClaimService {
    private final InsuranceClaimRepository insuranceClaimRepository;
    private final BookingInsuranceService bookingInsuranceService;
    private final AccountService accountService;

    public InsuranceClaim createInsuranceClaim(Long bookingId, InsuranceClaimRequestDTO requestDTO) {
        BookingInsurance bookingInsurance = bookingInsuranceService.findByBookingId(bookingId);
        InsuranceClaim insuranceClaim = InsuranceClaim.builder()
                .claimDescription(requestDTO.getClaimDescription())
                .bookingInsuranceId(bookingInsurance.getId())
                .claimDate(LocalDateTime.now())
                .claimStatus(ClaimStatus.PENDING)
                .build();
        return insuranceClaimRepository.save(insuranceClaim);
    }

    public Page<InsuranceClaim> getAllInsuranceClaims(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return insuranceClaimRepository.findAll(pageable);
    }

    public InsuranceClaim getInsuranceClaim(Long id) {
        return insuranceClaimRepository.findInsuranceClaimById(id)
                .orElseThrow(() -> new BadRequestException("Insurance Claim Not Found"));
    }

    public void updateStatus(Long id, InsuranceClaimRequestDTO requestDTO) {
        InsuranceClaim insuranceClaim = getInsuranceClaim(id);
        if (insuranceClaim.getClaimStatus() == ClaimStatus.APPROVED && requestDTO.getClaimStatus() == ClaimStatus.APPROVED) {
            throw new BadRequestException("This claim has already been approved and cannot be approved again");
        }
        BookingInsurance bookingInsurance = bookingInsuranceService.getBookingInsuranceById(insuranceClaim.getBookingInsuranceId());
        if (requestDTO.getClaimStatus() == ClaimStatus.APPROVED){
            Account account = accountService.getAccountById(bookingInsurance.getAccountId());
            account.setBalance(account.getBalance()+bookingInsurance.getCalculateCompensation());
        }
        insuranceClaim.setClaimStatus(requestDTO.getClaimStatus());
        insuranceClaim.setResolutionDate(LocalDateTime.now());
        insuranceClaimRepository.save(insuranceClaim);
    }

    public Page<InsuranceClaim> findByClaimStatus(ClaimStatus status, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return insuranceClaimRepository.findByClaimStatus(status, pageable);
    }

    public Page<InsuranceClaim> findByClaimDateBetween(LocalDate  startDate, LocalDate endDate, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atStartOfDay();
        return insuranceClaimRepository.findByClaimDateBetween(start, end, pageable);
    }
}
