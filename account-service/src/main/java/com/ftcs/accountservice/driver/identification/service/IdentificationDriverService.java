package com.ftcs.accountservice.driver.identification.service;

import com.ftcs.accountservice.driver.identification.dto.*;
import com.ftcs.accountservice.driver.identification.model.AddressDriver;
import com.ftcs.accountservice.driver.identification.model.DriverIdentification;
import com.ftcs.accountservice.driver.identification.repository.AddressDriverRepository;
import com.ftcs.accountservice.driver.identification.repository.DriverIdentificationRepository;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.common.feature.location.model.District;
import com.ftcs.common.feature.location.model.Province;
import com.ftcs.common.feature.location.model.Ward;
import com.ftcs.common.feature.location.repository.DistrictRepository;
import com.ftcs.common.feature.location.repository.ProvinceRepository;
import com.ftcs.common.feature.location.repository.WardRepository;
import com.ftcs.common.feature.location.service.LocationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class IdentificationDriverService {

    private final AddressDriverRepository addressDriverRepository;
    private final DriverIdentificationRepository driverIdentificationRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;

    private final LocationService locationService;


    public void addDriverIdentification(DriverIdentificationRequestDTO requestDTO, Integer accountId) {
        if (driverIdentificationRepository.existsByAccountId(accountId)) {
            throw new BadRequestException("Account already has a driver identification");
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

    public void updateDriverIdentificationByAccountId(DriverIdentificationRequestDTO requestDTO, Integer accountId) {

        DriverIdentification identification = findDriverIdentificationByAccountIdOptional(accountId);
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
        validateAddressCodes(addressDTO.getWardId(), addressDTO.getDistrictId(), addressDTO.getProvinceId());
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

    public DriverIdentification findDriverIdentificationByDriverIdentificationId(Integer driverIdentificationId) {
        return driverIdentificationRepository.findDriverIdentificationByDriverIdentificationId(driverIdentificationId)
                .orElseThrow(() -> new BadRequestException("Driver Identification not found"));
    }

    public DriverIdentification findDriverIdentificationByAccountIdOptional(Integer accountId) {
        return driverIdentificationRepository.findDriverIdentificationByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("Driver Identification not found"));
    }

    public DriverIdentificationResponseDTO findDriverIdentificationByAccountId(Integer accountId) {
        DriverIdentification identification = driverIdentificationRepository.findDriverIdentificationByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("Driver Identification not found"));

        AddressDriverResponseDTO permanentAddressDTO = null;
        AddressDriverResponseDTO temporaryAddressDTO = null;

        if (identification.getPermanentAddress() != null) {
            AddressDriver permanentAddress = addressDriverRepository.findById(identification.getPermanentAddress())
                    .orElseThrow(() -> new RuntimeException("Permanent Address not found"));
            permanentAddressDTO = mapToAddressDriverResponseDTOWithName(permanentAddress);
        }

        if (identification.getTemporaryAddress() != null) {
            AddressDriver temporaryAddress = addressDriverRepository.findById(identification.getTemporaryAddress())
                    .orElseThrow(() -> new RuntimeException("Temporary Address not found"));
            temporaryAddressDTO = mapToAddressDriverResponseDTOWithName(temporaryAddress);
        }

        return DriverIdentificationResponseDTO.builder()
                .driverIdentificationId(identification.getDriverIdentificationId())
                .accountId(identification.getAccountId())
                .idNumber(identification.getIdNumber())
                .status(identification.getStatus())
                .issueDate(identification.getIssueDate())
                .expiryDate(identification.getExpiryDate())
                .issuedBy(identification.getIssuedBy())
                .isVerified(identification.getIsVerified())
                .permanentAddress(permanentAddressDTO)
                .temporaryAddress(temporaryAddressDTO)
                .build();
    }

    private AddressDriverResponseDTO mapToAddressDriverResponseDTOWithName(AddressDriver addressDriver) {
        Province province = provinceRepository.findById(addressDriver.getProvinceId())
                .orElseThrow(() -> new RuntimeException("Province not found for ID: " + addressDriver.getProvinceId()));

        AddressDriverLocationDTO provinceLocation = new AddressDriverLocationDTO(province.getCode(), province.getName(), locationService.getProvinces());

        District district = districtRepository.findById(addressDriver.getDistrictId())
                .orElseThrow(() -> new RuntimeException("District not found for ID: " + addressDriver.getDistrictId()));

        AddressDriverLocationDTO districtLocation = new AddressDriverLocationDTO(district.getCode(), district.getName(), locationService.getDistrictsByProvince(province.getCode()));

        Ward ward = wardRepository.findById(addressDriver.getWardId())
                .orElseThrow(() -> new RuntimeException("Ward not found for ID: " + addressDriver.getWardId()));

        AddressDriverLocationDTO wardLocation = new AddressDriverLocationDTO(ward.getCode(), ward.getName(), locationService.getWardsByDistrict(district.getCode()));

        return AddressDriverResponseDTO.builder()
                .addressDriverId(addressDriver.getAddressDriverId())
                .streetAddress(addressDriver.getStreetAddress())
                .ward(wardLocation)
                .district(districtLocation)
                .province(provinceLocation)
                .addressType(addressDriver.getAddressType())
                .build();
    }




    private void validateAccountOwnership(Integer accountId, DriverIdentification identification) {
        if (!identification.getAccountId().equals(accountId)) {
            throw new BadRequestException("This driver identification does not belong to the specified account.");
        }
    }

    public void validateAddressCodes(Integer wardId, Integer districtId, Integer provinceId) {

        if (!provinceRepository.existsById(provinceId)) {
            throw new BadRequestException("Invalid Province ID: " + provinceId);
        }
        if (!districtRepository.existsById(districtId)) {
            throw new BadRequestException("Invalid District ID: " + districtId);
        }
        District district = districtRepository.findById(districtId)
                .orElseThrow(() -> new BadRequestException("District ID " + districtId + " does not belong to any Province"));
        if (!district.getProvinceCode().equals(provinceId)) {
            throw new BadRequestException("District ID " + districtId + " does not belong to Province ID " + provinceId);
        }

        if (!wardRepository.existsById(wardId)) {
            throw new BadRequestException("Invalid Ward ID: " + wardId);
        }

        Ward ward = wardRepository.findById(wardId)
                .orElseThrow(() -> new BadRequestException("Ward ID " + wardId + " does not belong to any District"));
        if (!ward.getDistrictCode().equals(districtId)) {
            throw new BadRequestException("Ward ID " + wardId + " does not belong to District ID " + districtId);
        }
    }
}