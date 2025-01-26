package com.ftcs.accountservice.driver.license.service;

import com.ftcs.accountservice.driver.license.dto.LicenseRequestDTO;
import com.ftcs.accountservice.driver.license.model.License;
import com.ftcs.accountservice.driver.license.repository.LicenseRepository;
import com.ftcs.accountservice.driver.shared.StatusDocumentType;
import com.ftcs.common.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class LicenseDriverService {

    private final LicenseRepository licenseRepository;

    public void updateLicense(Integer accountId, LicenseRequestDTO requestDTO, Integer licenseId) {
        License license = findLicenseByLicenseId(licenseId);
        validateAccountOwnership(accountId, license);
        updateLicenseDetails(license, requestDTO);
        licenseRepository.save(license);
    }

    public void updateLicenseByAccountId(Integer accountId, LicenseRequestDTO requestDTO) {
        License license = findLicenseByAccountId(accountId);
        validateAccountOwnership(accountId, license);
        updateLicenseDetails(license, requestDTO);
        licenseRepository.save(license);
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
        license.setStatus(StatusDocumentType.NEW);
    }

    public void createNewLicense(LicenseRequestDTO requestDTO, Integer accountId) {
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
                .status(StatusDocumentType.NEW)
                .build();
        licenseRepository.save(newLicense);
    }

    public void validateAccountOwnership(Integer accountId, License license) {
        if (!license.getAccountId().equals(accountId)) {
            throw new BadRequestException("This license does not belong to the specified account.");
        }
    }


}