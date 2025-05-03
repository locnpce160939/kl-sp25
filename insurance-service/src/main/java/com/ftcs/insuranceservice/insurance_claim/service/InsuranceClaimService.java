package com.ftcs.insuranceservice.insurance_claim.service;

import com.ftcs.accountservice.account.serivce.AccountService;
import com.ftcs.authservice.features.account.Account;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.common.upload.FileService;
import com.ftcs.common.upload.FolderEnum;
import com.ftcs.insuranceservice.booking_insurance.model.BookingInsurance;
import com.ftcs.insuranceservice.booking_insurance.service.BookingInsuranceService;
import com.ftcs.insuranceservice.insurance_claim.constant.ClaimStatus;
import com.ftcs.insuranceservice.insurance_claim.dto.InsuranceClaimRequestDTO;
import com.ftcs.insuranceservice.insurance_claim.model.InsuranceClaim;
import com.ftcs.insuranceservice.insurance_claim.repository.InsuranceClaimRepository;
import com.ftcs.insuranceservice.insurance_policy.model.InsurancePolicy;
import com.ftcs.insuranceservice.insurance_policy.service.InsurancePolicyService;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
@AllArgsConstructor
@Log4j2
public class InsuranceClaimService {
    private final InsuranceClaimRepository insuranceClaimRepository;
    private final BookingInsuranceService bookingInsuranceService;
    private final AccountService accountService;
    private final FileService fileService;

    private static final int MIN_IMAGES = 1;
    private static final int MAX_IMAGES = 5;

    public InsuranceClaim createInsuranceClaim(Long bookingId, InsuranceClaimRequestDTO requestDTO, List<MultipartFile> evidenceImages) {
        validateImages(evidenceImages);
        
        BookingInsurance bookingInsurance = bookingInsuranceService.findByBookingId(bookingId);
        InsuranceClaim insuranceClaim = InsuranceClaim.builder()
                .bookingId(bookingId)
                .claimDescription(requestDTO.getClaimDescription())
                .bookingInsuranceId(bookingInsurance.getId())
                .claimDate(LocalDateTime.now())
                .claimStatus(ClaimStatus.PENDING)
                .build();

        List<String> uploadedImages = handleMultipleFileUpload(evidenceImages);
        insuranceClaim.setEvidenceImageList(uploadedImages);

        return insuranceClaimRepository.save(insuranceClaim);
    }

    public void updateInsuranceClaim(Long id, InsuranceClaimRequestDTO requestDTO, List<MultipartFile> newImages) {
        validateImages(newImages);
        
        InsuranceClaim insuranceClaim = getInsuranceClaimByBookingId(id);
        
        // Delete old images if they exist
        List<String> oldImages = insuranceClaim.getEvidenceImageList();
        if (oldImages != null && !oldImages.isEmpty()) {
            oldImages.forEach(this::handleFileDelete);
        }
            
        // Upload new images
        List<String> uploadedImages = handleMultipleFileUpload(newImages);
        insuranceClaim.setEvidenceImageList(uploadedImages);

        insuranceClaim.setClaimDescription(requestDTO.getClaimDescription());
        insuranceClaimRepository.save(insuranceClaim);
    }

    private void validateImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) {
            throw new BadRequestException("At least " + MIN_IMAGES + " image is required for the insurance claim");
        }
        if (images.size() > MAX_IMAGES) {
            throw new BadRequestException("Maximum " + MAX_IMAGES + " images are allowed for the insurance claim");
        }
    }

    private List<String> handleMultipleFileUpload(List<MultipartFile> files) {
        List<String> uploadedFileNames = new ArrayList<>();
        
        for (MultipartFile file : files) {
            String originalFileName = file.getOriginalFilename();
            if (originalFileName == null) {
                throw new BadRequestException("Invalid file name");
            }

            CompletableFuture<String> uploadTask = new CompletableFuture<>();
            fileService.processFileAsync(
                file,
                originalFileName,
                FolderEnum.INSURANCE_CLAIM,
                fileName -> {
                    uploadedFileNames.add(fileName);
                    uploadTask.complete(fileName);
                }
            );
            
            try {
                uploadTask.get(); // Wait for upload to complete
            } catch (Exception e) {
                log.error("Error uploading file: " + originalFileName, e);
                throw new BadRequestException("Failed to upload file: " + originalFileName);
            }
        }
        
        return uploadedFileNames;
    }

    private void handleFileDelete(String fileName) {
        if (fileName != null && !fileName.isEmpty()) {
            fileService.processDeleteFile(fileName, FolderEnum.INSURANCE_CLAIM);
        }
    }

    public Page<InsuranceClaim> getAllInsuranceClaims(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return insuranceClaimRepository.findAll(pageable);
    }

    public InsuranceClaim getInsuranceClaim(Long id) {
        return insuranceClaimRepository.findInsuranceClaimById(id)
                .orElseThrow(() -> new BadRequestException("Insurance Claim Not Found"));
    }

    public InsuranceClaim getInsuranceClaimByBookingId(Long id) {
        return insuranceClaimRepository.findInsuranceClaimByBookingId(id)
                .orElseThrow(() -> new BadRequestException("Insurance Claim Not Found"));
    }

    public void updateStatus(Long id, InsuranceClaimRequestDTO requestDTO) {
        InsuranceClaim insuranceClaim = getInsuranceClaim(id);
        validate(insuranceClaim, requestDTO);
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

    public Page<InsuranceClaim> findByClaimDateBetween(LocalDate startDate, LocalDate endDate, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = endDate.atStartOfDay();
        return insuranceClaimRepository.findByClaimDateBetween(start, end, pageable);
    }

    public void validate(InsuranceClaim insuranceClaim, InsuranceClaimRequestDTO requestDTO) {
        if (insuranceClaim.getClaimStatus() == ClaimStatus.APPROVED && requestDTO.getClaimStatus() == ClaimStatus.APPROVED) {
            throw new BadRequestException("This claim has already been approved and cannot be approved again");
        }
    }
}
