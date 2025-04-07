package com.ftcs.insuranceservice.insurance_policy;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.insuranceservice.InsuranceURL;
import com.ftcs.insuranceservice.insurance_policy.dto.InsurancePolicyRequestDTO;
import com.ftcs.insuranceservice.insurance_policy.model.InsurancePolicy;
import com.ftcs.insuranceservice.insurance_policy.service.InsurancePolicyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(InsuranceURL.INSURANCE_POLICY)
public class InsurancePolicyController {
    private final InsurancePolicyService insurancePolicyService;

    @GetMapping
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'FINANCE')")
    public ApiResponse<Page<InsurancePolicy>> getAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                     @RequestParam(value = "size", defaultValue = "10") Integer size){
        return new ApiResponse<>(insurancePolicyService.getAllInsurancePolicies(page, size));
    }

    @GetMapping("/{policyId}")
    public ApiResponse<InsurancePolicy> getInsurancePolicy(@PathVariable("policyId") Long policyId){
        return new ApiResponse<>(insurancePolicyService.getInsurancePolicy(policyId));
    }

    @PostMapping
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'FINANCE')")
    public ApiResponse<InsurancePolicy> createInsurancePolicy(@RequestBody InsurancePolicyRequestDTO requestDTO){
        return new ApiResponse<>(insurancePolicyService.createInsurancePolicy(requestDTO));
    }

    @PutMapping("/{policyId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'FINANCE')")
    public ApiResponse<InsurancePolicy> update(@RequestBody InsurancePolicyRequestDTO requestDTO,
                                               @PathVariable("policyId") Long policyId){
        return new ApiResponse<>(insurancePolicyService.updateInsurancePolicy(requestDTO, policyId));
    }

    @DeleteMapping("/{policyId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'FINANCE')")
    public ApiResponse<?> delete(@PathVariable("policyId") Long policyId){
        insurancePolicyService.deleteInsurancePolicy(policyId);
        return new ApiResponse<>("Deleted InsurancePolicy");
    }
}
