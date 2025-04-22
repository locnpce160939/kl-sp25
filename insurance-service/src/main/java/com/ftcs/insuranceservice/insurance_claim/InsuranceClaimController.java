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
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(InsuranceURL.INSURANCE_CLAIM)
public class InsuranceClaimController {
    private final InsuranceClaimService insuranceClaimService;

    @PostMapping("/claims/{bookingId}")
    @PreAuthorize("hasPermission(null, 'CUSTOMER')")
    public ApiResponse<?> createClaim(@PathVariable("bookingId") Long bookingId,
                                    @RequestPart("data") InsuranceClaimRequestDTO requestDTO,
                                    @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        InsuranceClaim claim = insuranceClaimService.createInsuranceClaim(bookingId, requestDTO, images);
        return new ApiResponse<>("Insurance claim created successfully", claim);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'CUSTOMER')")
    public ApiResponse<?> updateClaim(@PathVariable("id") Long id,
                                    @RequestPart("data") InsuranceClaimRequestDTO requestDTO,
                                    @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        insuranceClaimService.updateInsuranceClaim(id, requestDTO, images);
        return new ApiResponse<>("Insurance claim updated successfully");
    }

    @GetMapping("/claims")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'FINANCE')")
    public ApiResponse<Page<InsuranceClaim>> getAllClaims(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                         @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(insuranceClaimService.getAllInsuranceClaims(page, size));
    }

    @GetMapping("/claims/{id}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'CUSTOMER')")
    public ApiResponse<InsuranceClaim> getClaimById(@PathVariable("id") Long id) {
        return new ApiResponse<>(insuranceClaimService.getInsuranceClaim(id));
    }

    @PutMapping("/claims/{id}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'FINANCE')")
    public ApiResponse<String> updateStatus(@PathVariable("id") Long id,
                                          @Valid @RequestBody InsuranceClaimRequestDTO requestDTO) {
        insuranceClaimService.updateStatus(id, requestDTO);
        return new ApiResponse<>("Insurance claim status updated successfully");
    }

    @GetMapping("/claims/status")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'FINANCE')")
    public ApiResponse<Page<InsuranceClaim>> getClaimsByStatus(@RequestParam("status") ClaimStatus status,
                                                              @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                              @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(insuranceClaimService.findByClaimStatus(status, page, size));
    }

    @GetMapping("/claims/date-range")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'FINANCE')")
    public ApiResponse<Page<InsuranceClaim>> getClaimsByDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(insuranceClaimService.findByClaimDateBetween(startDate, endDate, page, size));
    }
}

