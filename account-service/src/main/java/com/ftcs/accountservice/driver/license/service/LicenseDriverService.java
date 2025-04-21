package com.ftcs.accountservice.driver.license.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftcs.accountservice.driver.license.dto.LicenseRequestDTO;
import com.ftcs.accountservice.driver.license.dto.UpdateStatusLicenseRequestDTO;
import com.ftcs.accountservice.driver.license.model.License;
import com.ftcs.accountservice.driver.license.repository.LicenseRepository;
import com.ftcs.accountservice.driver.shared.StatusDocumentType;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.common.upload.FileService;
import com.ftcs.common.upload.FolderEnum;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Service
@AllArgsConstructor
@Log4j2
public class LicenseDriverService {

    private final LicenseRepository licenseRepository;
    private final FileService fileService;

    public void updateLicense(Integer accountId, LicenseRequestDTO requestDTO, Integer licenseId) {
        License license = findLicenseByLicenseId(licenseId);
        validateDate(requestDTO);
        validateAccountOwnership(accountId, license);
        updateLicenseDetails(license, requestDTO);
        licenseRepository.save(license);
    }

    public void updateLicenseByAccountId(LicenseRequestDTO requestDTO, Integer accountId,
                                         MultipartFile frontFile, MultipartFile backFile) {
        License license = findLicenseByAccountId(accountId);
        validateDate(requestDTO);
        validateAccountOwnership(accountId, license);
        if (frontFile != null) {
            handleFileDelete(license.getFrontView());
            handleFileUpload(frontFile, license::setFrontView);
        }

        if (backFile != null) {
            handleFileDelete(license.getBackView());
            handleFileUpload(backFile, license::setBackView);
        }

        updateLicenseDetails(license, requestDTO);
        licenseRepository.save(license);

        log.info("License updated successfully for accountId: {}, frontView: {}, backView: {}",
                accountId, license.getFrontView(), license.getBackView());
    }

    public void createNewLicense(LicenseRequestDTO requestDTO, Integer accountId, MultipartFile frontFile,
                                 MultipartFile backFile) {
        isExistingLicense(accountId);
        validateDate(requestDTO);
        License license = License.builder()
                .accountId(accountId)
                .licenseNumber(requestDTO.getLicenseNumber())
                .licenseType(requestDTO.getLicenseType())
                .issuedDate(requestDTO.getIssuedDate())
                .expiryDate(requestDTO.getExpiryDate())
                .issuingAuthority(requestDTO.getIssuingAuthority())
                .status(StatusDocumentType.NEW)
                .build();

        if (frontFile != null) {
            handleFileUpload(frontFile, license::setFrontView);
        }

        if (backFile != null) {
            handleFileUpload(backFile, license::setBackView);
        }
        licenseRepository.save(license);
    }

    private void handleFileUpload(MultipartFile file, Consumer<String> callback) {
        String originalFileName = file.getOriginalFilename();
        if (originalFileName == null) {
            throw new BadRequestException("Invalid file name");
        }

        CompletableFuture<Void> uploadTask = fileService.processFileAsync(
                file,
                originalFileName,
                FolderEnum.LICENSE_DRIVER,
                callback
        );

        uploadTask.join();
    }

    private void handleFileDelete(String fileName) {
        fileService.processDeleteFile(fileName, FolderEnum.LICENSE_DRIVER);
    }

    public License findLicenseByLicenseId(Integer licenseId) {
        return licenseRepository.findLicenseByLicenseId(licenseId)
                .orElseThrow(() -> new BadRequestException("License not found"));
    }

    public License findLicenseByAccountId(Integer accountId) {
        return licenseRepository.findLicenseByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("License not found"));
    }

    public void updateStatus(Integer licenseId, UpdateStatusLicenseRequestDTO requestDTO) {
//        boolean isValidStatus = Arrays.stream(StatusDocumentType.values())
//                .anyMatch(status -> status == requestDTO.getStatus());
//
//        if (!isValidStatus) {
//            throw new BadRequestException("Invalid status: " + requestDTO.getStatus());
//        }
        License license = findLicenseByLicenseId(licenseId);
        license.setStatus(requestDTO.getStatus());
        licenseRepository.save(license);
    }

    private void isExistingLicense(Integer accountId) {
        if (licenseRepository.existsByAccountId(accountId)) {
            throw new BadRequestException("Account already has a license.");
        }
    }

    private void updateLicenseDetails(License license, LicenseRequestDTO requestDTO) {
        license.setLicenseNumber(requestDTO.getLicenseNumber());
        license.setLicenseType(requestDTO.getLicenseType());
        license.setIssuedDate(requestDTO.getIssuedDate());
        license.setExpiryDate(requestDTO.getExpiryDate());
        license.setIssuingAuthority(requestDTO.getIssuingAuthority());
        license.setUpdateAt(LocalDateTime.now());
        license.setStatus(StatusDocumentType.NEW);
    }

    public void validateAccountOwnership(Integer accountId, License license) {
        if (!license.getAccountId().equals(accountId)) {
            throw new BadRequestException("This license does not belong to the specified account.");
        }
    }

    public void validateDate(LicenseRequestDTO requestDTO) {
        if(requestDTO.getIssuedDate().isAfter(LocalDateTime.now())) {
            throw new BadRequestException("The license date must be in the past");
        }
        if(requestDTO.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Expiration date must be in the future");
        }
        if(requestDTO.getIssuedDate().isAfter(requestDTO.getExpiryDate())) {
            throw new BadRequestException("The license issuance date must be before the expiration date");
        }
    }
}