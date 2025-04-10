package com.ftcs.insuranceservice.insurance_claim.dto;

import com.ftcs.insuranceservice.insurance_claim.constant.ClaimStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InsuranceClaimRequestDTO {
    private String claimDescription;
    private ClaimStatus claimStatus;
}
