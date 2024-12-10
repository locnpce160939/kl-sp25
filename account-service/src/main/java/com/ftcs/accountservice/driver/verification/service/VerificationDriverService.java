package com.ftcs.accountservice.driver.verification.service;

import com.ftcs.accountservice.driver.identification.model.DriverIdentification;
import com.ftcs.accountservice.driver.license.model.License;
import com.ftcs.accountservice.driver.vehicle.model.Vehicle;
import com.ftcs.accountservice.driver.identification.repository.DriverIdentificationRepository;
import com.ftcs.accountservice.driver.license.repository.LicenseRepository;
import com.ftcs.accountservice.driver.vehicle.repository.VehicleRepository;
import com.ftcs.accountservice.driver.verification.dto.VerifiedDocumentRequestDTO;
import com.ftcs.common.exception.BadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class VerificationDriverService {

    private final DriverIdentificationRepository driverIdentificationRepository;
    private final LicenseRepository licenseRepository;
    private final VehicleRepository vehicleRepository;

    public void updateVerificationStatus(Integer accountId, VerifiedDocumentRequestDTO requestDTO) {
        License license = licenseRepository.findLicenseByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("License not found for the specified account."));
        if (requestDTO.getLicenseVerified() != null) {
            license.setIsVerified(requestDTO.getLicenseVerified());
            license.setStatus(requestDTO.getStatus());
            licenseRepository.save(license);
        }

        Vehicle vehicle = vehicleRepository.findVehicleByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("Vehicle not found for the specified account."));
        if (requestDTO.getVehicleVerified() != null) {
            vehicle.setIsVerified(requestDTO.getVehicleVerified());
            vehicle.setStatus(requestDTO.getStatus());
            vehicleRepository.save(vehicle);
        }

        DriverIdentification identification = driverIdentificationRepository.findDriverIdentificationByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("Driver Identification not found for the specified account."));
        if (requestDTO.getDriverIdentificationVerified() != null) {
            identification.setIsVerified(requestDTO.getDriverIdentificationVerified());
            identification.setStatus(requestDTO.getStatus());
            driverIdentificationRepository.save(identification);
        }
    }

    public void validateRequiredInformation(Integer accountId) {
        boolean hasLicense = licenseRepository.existsByAccountId(accountId);
        boolean hasVehicle = vehicleRepository.existsByAccountId(accountId);
        boolean hasDriverIdentification = driverIdentificationRepository.existsByAccountId(accountId);
        if (!hasLicense && !hasVehicle && !hasDriverIdentification) {
            throw new BadRequestException("You must provide all information: License, Vehicle, and Driver Identification.");
        }
        if (hasLicense) {
            throw new BadRequestException("You already have a license.");
        }
        if (hasVehicle) {
            throw new BadRequestException("You already have a vehicle.");
        }
        throw new BadRequestException("You already have a driver identification.");
    }
}