package com.ftcs.registerdriver.service;

import com.ftcs.common.exception.BadRequestException;
import com.ftcs.common.feature.location.dto.LocationDto;
import com.ftcs.common.feature.location.repository.DistrictRepository;
import com.ftcs.common.feature.location.repository.ProvinceRepository;
import com.ftcs.common.feature.location.repository.WardRepository;
import com.ftcs.registerdriver.dto.*;
import com.ftcs.registerdriver.model.AddressDriver;
import com.ftcs.registerdriver.model.DriverIdentification;
import com.ftcs.registerdriver.model.License;
import com.ftcs.registerdriver.model.Vehicle;
import com.ftcs.registerdriver.repository.AddressDriverRepository;
import com.ftcs.registerdriver.repository.DriverIdentificationRepository;
import com.ftcs.registerdriver.repository.LicenseRepository;
import com.ftcs.registerdriver.repository.VehicleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RegisterDriverService {

    private final AddressDriverRepository addressDriverRepository;
    private final DriverIdentificationRepository driverIdentificationRepository;
    private final LicenseRepository licenseRepository;
    private final VehicleRepository vehicleRepository;
    private final DistrictRepository districtRepository;
    private final ProvinceRepository provinceRepository;
    private final WardRepository wardRepository;

    public void updateLicense(Integer accountId, LicenseRequestDTO requestDTO, Integer licenseId) {
        License license = findLicenseByLicenseId(licenseId);
        validateAccountOwnership(accountId, license);
        updateLicenseDetails(license, requestDTO);
        licenseRepository.save(license);
    }

    private License findLicenseByLicenseId(Integer licenseId) {
        return licenseRepository.findLicenseByLicenseId(licenseId)
                .orElseThrow(() -> new BadRequestException("License not found"));
    }

    private void validateAccountOwnership(Integer accountId, License license) {
        if (!license.getAccountId().equals(accountId)) {
            throw new BadRequestException("This license does not belong to the specified account.");
        }
    }

    private void updateLicenseDetails(License license, LicenseRequestDTO requestDTO) {
        license.setLicenseNumber(requestDTO.getLicenseNumber());
        license.setLicenseType(requestDTO.getLicenseType());
        license.setIssuedDate(requestDTO.getIssuedDate());
        license.setExpiryDate(requestDTO.getExpiryDate());
        license.setIssuingAuthority(requestDTO.getIssuingAuthority());
        license.setUpdateAt(LocalDateTime.now());
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
                .status("Pending")
                .isVerified(false)
                .build();
        licenseRepository.save(newLicense);
    }

    public void createNewVehicle(VehicleRequestDTO requestDTO, Integer accountId) {
        if (vehicleRepository.existsByAccountId(accountId)) {
            throw new BadRequestException("Account already has a vehicle.");
        }
        Vehicle newVehicle = Vehicle.builder()
                .accountId(accountId)
                .licensePlate(requestDTO.getLicensePlate())
                .vehicleType(requestDTO.getVehicleType())
                .make(requestDTO.getMake())
                .model(requestDTO.getModel())
                .year(requestDTO.getYear())
                .capacity(requestDTO.getCapacity())
                .dimensions(requestDTO.getDimensions())
                .status("Pending")
                .isVerified(false)
                .insuranceStatus(requestDTO.getInsuranceStatus())
                .registrationExpiryDate(requestDTO.getRegistrationExpiryDate())
                .build();
        vehicleRepository.save(newVehicle);
    }

    public void updateVehicle(Integer accountId, VehicleRequestDTO requestDTO, Integer vehicleId) {
        Vehicle vehicle = findVehicleByVehicleId(vehicleId);
        validateAccountOwnership(accountId, vehicle);
        updateVehicleDetails(vehicle, requestDTO);
        vehicleRepository.save(vehicle);
    }

    private Vehicle findVehicleByVehicleId(Integer vehicleId) {
        return vehicleRepository.findVehicleByVehicleId(vehicleId)
                .orElseThrow(() -> new BadRequestException("Vehicle not found"));
    }

    private void validateAccountOwnership(Integer accountId, Vehicle vehicle) {
        if (!vehicle.getAccountId().equals(accountId)) {
            throw new BadRequestException("This vehicle does not belong to the specified account.");
        }
    }

    private void updateVehicleDetails(Vehicle vehicle, VehicleRequestDTO requestDTO) {
        vehicle.setLicensePlate(requestDTO.getLicensePlate());
        vehicle.setVehicleType(requestDTO.getVehicleType());
        vehicle.setMake(requestDTO.getMake());
        vehicle.setModel(requestDTO.getModel());
        vehicle.setYear(requestDTO.getYear());
        vehicle.setCapacity(requestDTO.getCapacity());
        vehicle.setDimensions(requestDTO.getDimensions());
        vehicle.setInsuranceStatus(requestDTO.getInsuranceStatus());
        vehicle.setRegistrationExpiryDate(requestDTO.getRegistrationExpiryDate());
        vehicle.setUpdateAt(LocalDateTime.now());
    }

    public void addDriverIdentification(DriverIdentificationRequestDTO requestDTO, Integer accountId) {
        if (driverIdentificationRepository.existsByAccountId(accountId)) {
            throw new BadRequestException("Account already has a driver identification.");
        }
        Integer permanentAddressId = addAddressDriver(createAddressDriverRequestDTO(requestDTO, "Permanent Address", true));
        Integer temporaryAddressId = addAddressDriver(createAddressDriverRequestDTO(requestDTO, "Temporary Address", false));

        DriverIdentification identification = createNewDriverIdentification(accountId);

        updateDriverIdentificationDetails(identification, requestDTO, permanentAddressId, temporaryAddressId);
        driverIdentificationRepository.save(identification);
    }

    private DriverIdentification createNewDriverIdentification(Integer accountId) {
        return DriverIdentification.builder()
                .accountId(accountId)
                .status("Pending")
                .build();
    }

    private void updateDriverIdentificationDetails(DriverIdentification identification, DriverIdentificationRequestDTO requestDTO, Integer permanentAddressId, Integer temporaryAddressId) {
        identification.setIdNumber(requestDTO.getIdNumber());
        identification.setPermanentAddress(permanentAddressId);
        identification.setTemporaryAddress(temporaryAddressId);
        identification.setIssueDate(requestDTO.getIssueDate());
        identification.setExpiryDate(requestDTO.getExpiryDate());
        identification.setIssuedBy(requestDTO.getIssuedBy());
        identification.setUpdateAt(LocalDateTime.now());
    }

    public void updateDriverIdentification(DriverIdentificationRequestDTO requestDTO, Integer driverIdentificationId, Integer accountId) {
        DriverIdentification identification = findDriverIdentificationByDriverIdentificationId(driverIdentificationId);
        validateAccountOwnership(accountId, identification);

        Integer permanentAddressId = identification.getPermanentAddress();
        Integer temporaryAddressId = identification.getTemporaryAddress();

        if (permanentAddressId != null) {
            updateAddressDriver(permanentAddressId, createAddressDriverRequestDTO(requestDTO, "Permanent Address", true));
        }
        if (temporaryAddressId != null) {
            updateAddressDriver(temporaryAddressId, createAddressDriverRequestDTO(requestDTO, "Temporary Address", false));
        }

        updateDriverIdentificationDetails(identification, requestDTO, permanentAddressId, temporaryAddressId);
        driverIdentificationRepository.save(identification);
    }

    private AddressDriverRequestDTO createAddressDriverRequestDTO(DriverIdentificationRequestDTO requestDTO, String addressType, boolean isPermanent) {
        return new AddressDriverRequestDTO(
                isPermanent ? requestDTO.getPermanentAddressWard() : requestDTO.getTemporaryAddressWard(),
                isPermanent ? requestDTO.getPermanentAddressDistrict() : requestDTO.getTemporaryAddressDistrict(),
                isPermanent ? requestDTO.getPermanentAddressProvince() : requestDTO.getTemporaryAddressProvince(),
                isPermanent ? requestDTO.getPermanentStreetAddress() : requestDTO.getTemporaryStreetAddress(),
                addressType
        );
    }

    public void updateAddressDriver(Integer addressDriverId, AddressDriverRequestDTO addressDTO) {
        AddressDriver existingAddress = addressDriverRepository.findById(addressDriverId)
                .orElseThrow(() -> new RuntimeException("Address not found"));

        existingAddress.setStreetAddress(addressDTO.getStreetAddress());
        existingAddress.setWardId(addressDTO.getWardId());
        existingAddress.setDistrictId(addressDTO.getDistrictId());
        existingAddress.setProvinceId(addressDTO.getProvinceId());

        addressDriverRepository.save(existingAddress);
    }

    public Integer addAddressDriver(AddressDriverRequestDTO addressDTO) {
        validateAddressCodes(addressDTO.getWardId(), addressDTO.getDistrictId(), addressDTO.getProvinceId());

        AddressDriver newAddress = AddressDriver.builder()
                .streetAddress(addressDTO.getStreetAddress())
                .wardId(addressDTO.getWardId())
                .districtId(addressDTO.getDistrictId())
                .provinceId(addressDTO.getProvinceId())
                .addressType(addressDTO.getAddressType())
                .build();

        AddressDriver savedAddress = addressDriverRepository.save(newAddress);
        return savedAddress.getAddressDriverId();
    }

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

        if (hasLicense) {
            throw new BadRequestException("You already have a license.");
        }
        if (hasVehicle) {
            throw new BadRequestException("You already have a vehicle.");
        }
        if (hasDriverIdentification) {
            throw new BadRequestException("You already have a driver identification.");
        }

        if (!hasLicense || !hasVehicle || !hasDriverIdentification) {
            throw new BadRequestException("You must provide all information: License, Vehicle, and Driver Identification.");
        }
    }


    private DriverIdentification findDriverIdentificationByDriverIdentificationId(Integer driverIdentificationId) {
        return driverIdentificationRepository.findDriverIdentificationByDriverIdentificationId(driverIdentificationId)
                .orElseThrow(() -> new BadRequestException("Driver Identification not found"));
    }

    private void validateAccountOwnership(Integer accountId, DriverIdentification identification) {
        if (!identification.getAccountId().equals(accountId)) {
            throw new BadRequestException("This driver identification does not belong to the specified account.");
        }
    }

    private void validateAddressCodes(Integer wardId, Integer districtId, Integer provinceId) {
        List<LocationDto> provinces = getProvinces();
        if (!provinces.stream().anyMatch(province -> province.getCode().equals(provinceId))) {
            throw new BadRequestException("Invalid Province ID: " + provinceId);
        }

        List<LocationDto> districts = getDistrictsByProvince(provinceId);
        if (!districts.stream().anyMatch(district -> district.getCode().equals(districtId))) {
            throw new BadRequestException("District ID " + districtId + " does not belong to Province ID " + provinceId);
        }

        List<LocationDto> wards = getWardsByDistrict(districtId);
        if (!wards.stream().anyMatch(ward -> ward.getCode().equals(wardId))) {
            throw new BadRequestException("Ward ID " + wardId + " does not belong to District ID " + districtId);
        }
    }

    public List<LocationDto> getProvinces() {
        return provinceRepository.findAll().stream()
                .map(province -> new LocationDto(province.getCode(), province.getFullName()))
                .collect(Collectors.toList());
    }

    public List<LocationDto> getDistrictsByProvince(Integer provinceCode) {
        return districtRepository.findByProvinceCode(provinceCode).stream()
                .map(district -> new LocationDto(district.getCode(), district.getFullName()))
                .collect(Collectors.toList());
    }

    public List<LocationDto> getWardsByDistrict(Integer districtCode) {
        return wardRepository.findByDistrictCode(districtCode).stream()
                .map(ward -> new LocationDto(ward.getCode(), ward.getFullName()))
                .collect(Collectors.toList());
    }
}