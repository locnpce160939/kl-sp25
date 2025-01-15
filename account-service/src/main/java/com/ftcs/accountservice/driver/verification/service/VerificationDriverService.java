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

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class VerificationDriverService {

    private final DriverIdentificationRepository driverIdentificationRepository;
    private final LicenseRepository licenseRepository;
    private final VehicleRepository vehicleRepository;

    public void updateVerificationStatus(Integer accountId, VerifiedDocumentRequestDTO requestDTO) {
        // Update License Verification
        License license = licenseRepository.findLicenseByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("License not found for the specified account."));
        if (requestDTO.getLicenseVerified() != null) {
            license.setIsVerified(requestDTO.getLicenseVerified());
            license.setStatus(requestDTO.getStatus());
            licenseRepository.save(license);
        }

        // Update Vehicle Verifications
        List<Vehicle> vehicles = vehicleRepository.findVehiclesByAccountId(accountId);
        if (vehicles.isEmpty()) {
            throw new BadRequestException("No vehicles found for the specified account.");
        }
        if (requestDTO.getVehicleVerified() != null) {
            for (Vehicle vehicle : vehicles) {
                vehicle.setIsVerified(requestDTO.getVehicleVerified());
                vehicle.setStatus(requestDTO.getStatus());
            }
            vehicleRepository.saveAll(vehicles);
        }

        // Update Driver Identification Verification
        DriverIdentification identification = driverIdentificationRepository.findDriverIdentificationByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("Driver Identification not found for the specified account."));
        if (requestDTO.getDriverIdentificationVerified() != null) {
            identification.setIsVerified(requestDTO.getDriverIdentificationVerified());
            identification.setStatus(requestDTO.getStatus());
            driverIdentificationRepository.save(identification);
        }
    }

    public List<String> validateRequiredInformation(Integer accountId) {
        boolean hasLicense = licenseRepository.existsByAccountId(accountId);
        boolean hasVehicles = vehicleRepository.existsByAccountId(accountId);
        boolean hasDriverIdentification = driverIdentificationRepository.existsByAccountId(accountId);

        List<String> errorMessages = new ArrayList<>();

        if (!hasLicense) {
            errorMessages.add("You must provide a License.");
        }
        if (!hasVehicles) {
            errorMessages.add("You must provide at least one Vehicle.");
        }
        if (!hasDriverIdentification) {
            errorMessages.add("You must provide a Driver Identification.");
        }

        if (hasLicense && hasVehicles && hasDriverIdentification) {
            errorMessages.add("You already have a license.");
            errorMessages.add("You already have vehicles.");
            errorMessages.add("You already have a driver identification.");
        }

        return errorMessages;
    }
}
