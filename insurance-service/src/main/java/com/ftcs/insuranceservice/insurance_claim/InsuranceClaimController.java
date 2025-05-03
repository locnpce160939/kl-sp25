package com.ftcs.insuranceservice.insurance_claim;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftcs.common.dto.ApiResponse;
import com.ftcs.insuranceservice.InsuranceURL;
import com.ftcs.insuranceservice.insurance_claim.constant.ClaimStatus;
import com.ftcs.insuranceservice.insurance_claim.dto.InsuranceClaimRequestDTO;
import com.ftcs.insuranceservice.insurance_claim.model.InsuranceClaim;
import com.ftcs.insuranceservice.insurance_claim.service.InsuranceClaimService;
import com.ftcs.insuranceservice.insurance_claim.service.InsuranceClaimExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(InsuranceURL.INSURANCE_CLAIM)
public class InsuranceClaimController {
    private final InsuranceClaimService insuranceClaimService;
    private final InsuranceClaimExportService insuranceClaimExportService;
    private final ObjectMapper objectMapper;

    @PutMapping("/{id}")
    @PreAuthorize("hasPermission(null, 'CUSTOMER')")
    public ApiResponse<?> updateClaim(@PathVariable("id") Long id,
                                    @RequestPart("data") String requestDTO,
                                    @RequestPart("images") List<MultipartFile> images) throws JsonProcessingException {
        insuranceClaimService.updateInsuranceClaim(id, objectMapper.readValue(requestDTO, InsuranceClaimRequestDTO.class), images);
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

    @GetMapping("/claims/booking/{id}")
    public ApiResponse<InsuranceClaim> getClaimByBookingId(@PathVariable("id") Long id) {
        return new ApiResponse<>(insuranceClaimService.getInsuranceClaimByBookingId(id));
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

    @GetMapping("/export/{claimId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'FINANCE')")
    public ResponseEntity<byte[]> exportClaimById(@PathVariable("claimId") Long claimId) {
        try {
            byte[] excelBytes = insuranceClaimExportService.exportClaimById(claimId);
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "claim_" + claimId + "_" + timestamp + ".xlsx";

            return createExcelResponse(excelBytes, filename);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    private ResponseEntity<byte[]> createExcelResponse(byte[] excelBytes, String filename) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
        headers.setContentDispositionFormData("attachment", filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);
    }
}

