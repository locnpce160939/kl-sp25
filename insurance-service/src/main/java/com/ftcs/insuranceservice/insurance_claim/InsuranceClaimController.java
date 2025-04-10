package com.ftcs.insuranceservice.insurance_claim;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.insuranceservice.InsuranceURL;
import com.ftcs.insuranceservice.insurance_claim.constant.ClaimStatus;
import com.ftcs.insuranceservice.insurance_claim.dto.InsuranceClaimRequestDTO;
import com.ftcs.insuranceservice.insurance_claim.model.InsuranceClaim;
import com.ftcs.insuranceservice.insurance_claim.service.InsuranceClaimService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping(InsuranceURL.INSURANCE_CLAIM)
public class InsuranceClaimController {
    private final InsuranceClaimService insuranceClaimService;

    @PostMapping("/booking/{bookingId}")
    public ApiResponse<InsuranceClaim> createInsuranceClaim(@PathVariable("bookingId") Long bookingId,
                                                            @RequestBody InsuranceClaimRequestDTO requestDTO) {
        return new ApiResponse<>(insuranceClaimService.createInsuranceClaim(bookingId, requestDTO));
    }

    @GetMapping
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'FINANCE')")
    public ApiResponse<Page<InsuranceClaim>> getAllInsuranceClaims(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                   @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(insuranceClaimService.getAllInsuranceClaims(page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<InsuranceClaim> getInsuranceClaim(@PathVariable("id") Long id) {
        return new ApiResponse<>(insuranceClaimService.getInsuranceClaim(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'FINANCE')")
    public ApiResponse<String> updateStatus(@PathVariable("id") Long id,
                                            @RequestBody InsuranceClaimRequestDTO requestDTO) {
        insuranceClaimService.updateStatus(id, requestDTO);
        return new ApiResponse<>("Insurance claim status updated successfully");
    }

    @GetMapping("/status")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'FINANCE')")
    public ApiResponse<Page<InsuranceClaim>> getByStatus(@RequestParam("status") ClaimStatus status,
                                                         @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                         @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(insuranceClaimService.findByClaimStatus(status, page, size));
    }

    @GetMapping("/date-range")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'FINANCE')")
    public ApiResponse<Page<InsuranceClaim>> getByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDate  endDate,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(insuranceClaimService.findByClaimDateBetween(startDate, endDate, page, size));
    }
}

