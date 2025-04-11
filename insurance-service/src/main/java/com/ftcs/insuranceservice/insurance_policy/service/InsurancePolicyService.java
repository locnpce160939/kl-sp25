package com.ftcs.insuranceservice.insurance_policy.service;

import com.ftcs.common.exception.BadRequestException;
import com.ftcs.insuranceservice.booking_type.model.BookingType;
import com.ftcs.insuranceservice.booking_type.service.BookingTypeService;
import com.ftcs.insuranceservice.insurance_policy.dto.InsurancePolicyRequestDTO;
import com.ftcs.insuranceservice.insurance_policy.model.InsurancePolicy;
import com.ftcs.insuranceservice.insurance_policy.repository.InsurancePolicyRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class InsurancePolicyService {
    private final InsurancePolicyRepository insurancePolicyRepository;
    private final BookingTypeService bookingTypeService;

    public InsurancePolicy createInsurancePolicy(InsurancePolicyRequestDTO requestDTO) {
        bookingTypeService.getBookingType(requestDTO.getBookingType());
        InsurancePolicy insurancePolicy = InsurancePolicy.builder()
                .name(requestDTO.getName())
                .bookingType(requestDTO.getBookingType())
                .coverageDetails(requestDTO.getCoverageDetails())
                .description(requestDTO.getDescription())
                .compensationPercentage(requestDTO.getCompensationPercentage())
                .premiumPercentage(requestDTO.getPremiumPercentage())
                .build();
        insurancePolicyRepository.save(insurancePolicy);
        return insurancePolicy;
    }

    public InsurancePolicy updateInsurancePolicy(InsurancePolicyRequestDTO requestDTO, Long policyId) {
        InsurancePolicy insurancePolicy = getInsurancePolicy(policyId);
        insurancePolicy.setName(requestDTO.getName());
        insurancePolicy.setBookingType(requestDTO.getBookingType());
        insurancePolicy.setCoverageDetails(requestDTO.getCoverageDetails());
        insurancePolicy.setDescription(requestDTO.getDescription());
        insurancePolicy.setCompensationPercentage(requestDTO.getCompensationPercentage());
        insurancePolicy.setPremiumPercentage(requestDTO.getPremiumPercentage());
        insurancePolicyRepository.save(insurancePolicy);
        return insurancePolicy;
    }

    public InsurancePolicy getInsurancePolicy(Long policyId) {
        return insurancePolicyRepository.findInsurancePolicyByPolicyId(policyId)
                .orElseThrow(() -> new BadRequestException("Policy not found"));
    }

    public Page<InsurancePolicy> getAllInsurancePolicies(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return insurancePolicyRepository.findAll(pageable);
    }

    public void deleteInsurancePolicy(Long policyId) {
        insurancePolicyRepository.deleteById(policyId);
    }

    public List<InsurancePolicy> getInsurancePolicyByBookingType(Long bookingTypeId) {
        return  insurancePolicyRepository.findByBookingType(bookingTypeId);
    }

}
