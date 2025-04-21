package com.ftcs.accountservice.driver.verification.service;

import com.ftcs.accountservice.account.serivce.AccountService;
import com.ftcs.accountservice.driver.identification.model.DriverIdentification;
import com.ftcs.accountservice.driver.license.model.License;
import com.ftcs.accountservice.driver.shared.StatusDocumentType;
import com.ftcs.accountservice.driver.vehicle.model.Vehicle;
import com.ftcs.accountservice.driver.identification.repository.DriverIdentificationRepository;
import com.ftcs.accountservice.driver.license.repository.LicenseRepository;
import com.ftcs.accountservice.driver.vehicle.repository.VehicleRepository;
import com.ftcs.accountservice.driver.verification.dto.StatusDocumentsDTO;
import com.ftcs.accountservice.driver.verification.dto.VerifiedDocumentRequestDTO;
import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.authservice.features.account.contacts.StatusAccount;
import com.ftcs.common.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class VerificationDriverService {

    private final DriverIdentificationRepository identificationRepository;
    private final LicenseRepository licenseRepository;
    private final VehicleRepository vehicleRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    public void updateVerificationStatus(Integer accountId, VerifiedDocumentRequestDTO requestDTO) {
        updateLicenseVerification(accountId, requestDTO);
        updateVehicleVerification(accountId, requestDTO);
        updateDriverIdentificationVerification(accountId, requestDTO);
    }

    private void updateLicenseVerification(Integer accountId, VerifiedDocumentRequestDTO requestDTO) {
        License license = licenseRepository.findLicenseByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("License not found for the specified account."));
        //Optional.ofNullable(requestDTO.getLicenseVerified()).ifPresent(verified -> {
        //    license.setStatus(requestDTO.getLicenseVerified());
        if (requestDTO.getLicenseVerified() != null) {
            license.setStatus(requestDTO.getLicenseVerified());
            licenseRepository.save(license);
        }
    }

    private void updateVehicleVerification(Integer accountId, VerifiedDocumentRequestDTO requestDTO) {
        List<Vehicle> vehicles = vehicleRepository.findVehiclesByAccountId(accountId);
        if (vehicles.isEmpty()) {
            throw new BadRequestException("No vehicles found for the specified account.");
        }
       // Optional.ofNullable(requestDTO.getVehicleVerified()).ifPresent(verified -> {
       //     vehicles.forEach(vehicle -> {
       //         vehicle.setStatus(requestDTO.getVehicleVerified());
       //    });
        if (requestDTO.getVehicleVerified() != null) {
            for (Vehicle vehicle : vehicles) {
                vehicle.setStatus(requestDTO.getLicenseVerified());
            }
            vehicleRepository.saveAll(vehicles);
        }
    }

    private void updateDriverIdentificationVerification(Integer accountId, VerifiedDocumentRequestDTO requestDTO) {
        DriverIdentification identification = identificationRepository.findDriverIdentificationByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("Driver Identification not found for the specified account."));
        Optional.ofNullable(requestDTO.getDriverIdentificationVerified()).ifPresent(verified -> {
            identification.setStatus(requestDTO.getDriverIdentificationVerified());
            identificationRepository.save(identification);
        });
    }

    public StatusDocumentsDTO validateRequiredInformation(Integer accountId) {
        boolean hasLicense = licenseRepository.existsByAccountIdAndStatus(accountId, StatusDocumentType.APPROVED);
        boolean hasVehicle = vehicleRepository.existsByAccountIdAndStatus(accountId, StatusDocumentType.APPROVED);
        boolean hasIdentification = identificationRepository.existsByAccountIdAndStatus(accountId, StatusDocumentType.APPROVED);

        return new StatusDocumentsDTO(hasLicense, hasVehicle, hasIdentification);
    }

    public boolean checkRequiredInformation(Integer accountId) {
        boolean hasLicense = licenseRepository.existsByAccountIdAndStatus(accountId, StatusDocumentType.APPROVED);
        boolean hasVehicle = vehicleRepository.existsByAccountIdAndStatus(accountId, StatusDocumentType.APPROVED);
        boolean hasIdentification = identificationRepository.existsByAccountIdAndStatus(accountId, StatusDocumentType.APPROVED);
        Account account = accountService.getAccountById(accountId);
        
        boolean allApproved = hasLicense && hasVehicle && hasIdentification;
        
        if (allApproved) {
            account.setStatus(StatusAccount.ACTIVE);
        } else {
            account.setStatus(StatusAccount.PENDING);
        }
        
        accountRepository.save(account);
        return allApproved;
    }
}