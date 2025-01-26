package com.ftcs.accountservice.driver.license.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftcs.accountservice.driver.license.dto.LicenseRequestDTO;
import com.ftcs.accountservice.driver.license.model.License;
import com.ftcs.accountservice.driver.license.repository.LicenseRepository;
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

@Service
@AllArgsConstructor
@Log4j2
public class LicenseDriverService {

    private final LicenseRepository licenseRepository;
    private final FileService fileService;

    public void updateLicense(Integer accountId, LicenseRequestDTO requestDTO, Integer licenseId) {
        License license = findLicenseByLicenseId(licenseId);
        validateAccountOwnership(accountId, license);
        updateLicenseDetails(license, requestDTO);
        licenseRepository.save(license);
    }

    public void updateLicenseByAccountId(Integer accountId, String requestDTOJson,
                                         MultipartFile frontFile, MultipartFile backFile, FolderEnum folderEnum) {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        LicenseRequestDTO requestDTO;
        try {
            requestDTO = objectMapper.readValue(requestDTOJson, LicenseRequestDTO.class);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid JSON in requestDTO");
        }

        License license = findLicenseByAccountId(accountId);
        validateAccountOwnership(accountId, license);

        String[] frontViewUrl = new String[1];
        String[] backViewUrl = new String[1];

        if (frontFile != null) {
            String frontOriginalFileName = frontFile.getOriginalFilename();
            if (frontOriginalFileName == null) {
                throw new BadRequestException("Invalid front file name");
            }

            CompletableFuture<Void> frontUploadTask = fileService.processFileAsync(
                    frontFile,
                    frontOriginalFileName,
                    folderEnum,
                    frontUrl -> {
                        log.info("Front file uploaded successfully: {}", frontUrl);
                        frontViewUrl[0] = frontUrl;  // Gán giá trị vào vị trí mảng
                    }
            );

            frontUploadTask.join();
        }

        if (backFile != null) {
            String backOriginalFileName = backFile.getOriginalFilename();
            if (backOriginalFileName == null) {
                throw new BadRequestException("Invalid back file name");
            }

            CompletableFuture<Void> backUploadTask = fileService.processFileAsync(
                    backFile,
                    backOriginalFileName,
                    folderEnum,
                    backUrl -> {
                        log.info("Back file uploaded successfully: {}", backUrl);
                        backViewUrl[0] = backUrl;  // Gán giá trị vào vị trí mảng
                    }
            );

            backUploadTask.join();
        }

        updateLicenseDetails(license, requestDTO);
        license.setFrontView(frontViewUrl[0]);
        license.setBackView(backViewUrl[0]);

        licenseRepository.save(license);

        log.info("License updated successfully for accountId: {}, frontView: {}, backView: {}",
                accountId, frontViewUrl[0], backViewUrl[0]);
    }




    public License findLicenseByLicenseId(Integer licenseId) {
        return licenseRepository.findLicenseByLicenseId(licenseId)
                .orElseThrow(() -> new BadRequestException("License not found"));
    }

    public License findLicenseByAccountId(Integer accountId) {
        return licenseRepository.findLicenseByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("License not found"));
    }

    private void updateLicenseDetails(License license, LicenseRequestDTO requestDTO) {
        license.setLicenseNumber(requestDTO.getLicenseNumber());
        license.setLicenseType(requestDTO.getLicenseType());
        license.setIssuedDate(requestDTO.getIssuedDate());
        license.setExpiryDate(requestDTO.getExpiryDate());
        license.setIssuingAuthority(requestDTO.getIssuingAuthority());
        license.setUpdateAt(LocalDateTime.now());
    }

    public void createNewLicense(String requestDTOJson, Integer accountId, MultipartFile frontFile,
                                 MultipartFile backFile, FolderEnum folderEnum) {


        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        LicenseRequestDTO requestDTO;
        try {
            requestDTO = objectMapper.readValue(requestDTOJson, LicenseRequestDTO.class);
        } catch (JsonProcessingException e) {
            throw new BadRequestException("Invalid JSON in requestDTO");
        }

        String frontOriginalFileName = frontFile.getOriginalFilename();
        if (frontOriginalFileName == null) {
            throw new BadRequestException("Invalid front file name");
        }

        String backOriginalFileName = backFile.getOriginalFilename();
        if (backOriginalFileName == null) {
            throw new BadRequestException("Invalid back file name");
        }

        CompletableFuture<Void> frontUploadTask = fileService.processFileAsync(
                frontFile,
                frontOriginalFileName,
                folderEnum,
                frontUrl -> log.info("Front file uploaded successfully: {}", frontUrl)
        );

        CompletableFuture<Void> backUploadTask = fileService.processFileAsync(
                backFile,
                backOriginalFileName,
                folderEnum,
                backUrl -> log.info("Back file uploaded successfully: {}", backUrl)
        );

        CompletableFuture.allOf(frontUploadTask, backUploadTask).join();

        String frontViewUrl = fileService.getInternalMinio().getUrlFile();
        String backViewUrl = fileService.getInternalMinio().getUrlFile();

        if (licenseRepository.existsByAccountId(accountId)) {
            throw new BadRequestException("Account already has a license.");
        }

        License newLicense = License.builder()
                .accountId(accountId)
                .licenseNumber(requestDTO.getLicenseNumber())
                .licenseType(requestDTO.getLicenseType())
                .issuedDate(requestDTO.getIssuedDate())
                .expiryDate(requestDTO.getExpiryDate())
                .issuingAuthority(requestDTO.getIssuingAuthority())
                .status("Pending")
                .frontView(frontViewUrl)
                .backView(backViewUrl)
                .build();
        licenseRepository.save(newLicense);
        log.info("License created successfully for accountId: {}, frontView: {}, backView: {}",
                accountId, frontViewUrl, backViewUrl);
    }

    public void validateAccountOwnership(Integer accountId, License license) {
        if (!license.getAccountId().equals(accountId)) {
            throw new BadRequestException("This license does not belong to the specified account.");
        }
    }
}