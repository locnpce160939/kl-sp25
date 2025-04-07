package com.ftcs.insuranceservice.insurance_policy.dto;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsurancePolicyRequestDTO {
    private String name;
    private String description;
    private String coverageDetails;
    private Long bookingType;
    private Double premiumPercentage;
    private Double compensationPercentage;
}
