package com.ftcs.accountservice.driver.identification.service;

import com.ftcs.accountservice.customer.address.service.AddressService;
import com.ftcs.accountservice.driver.identification.dto.*;
import com.ftcs.accountservice.driver.identification.model.AddressDriver;
import com.ftcs.accountservice.driver.identification.repository.AddressDriverRepository;
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

@Service
@AllArgsConstructor
public class AddressIdentificationService {

    private final AddressDriverRepository addressDriverRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;
    private final LocationService locationService;

    private final AddressService addressService;

    public Integer addAddressDriver(AddressDriverRequestDTO addressDTO) {
        addressService.validateAddressCodes(addressDTO.getWardId(), addressDTO.getDistrictId(), addressDTO.getProvinceId());
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

    public void updateAddressDriver(Integer addressDriverId, AddressDriverRequestDTO addressDTO) {
        addressService.validateAddressCodes(addressDTO.getWardId(), addressDTO.getDistrictId(), addressDTO.getProvinceId());
        AddressDriver existingAddress = addressDriverRepository.findById(addressDriverId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        existingAddress.setStreetAddress(addressDTO.getStreetAddress());
        existingAddress.setWardId(addressDTO.getWardId());
        existingAddress.setDistrictId(addressDTO.getDistrictId());
        existingAddress.setProvinceId(addressDTO.getProvinceId());
        addressDriverRepository.save(existingAddress);
    }

    public AddressDriverResponseDTO mapToAddressDriverResponseDTOWithName(AddressDriver addressDriver) {
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

    public AddressDriver getAddressDriverById(Integer addressDriverId) {
        return addressDriverRepository.findById(addressDriverId)
                .orElseThrow(() -> new BadRequestException("Address not found"));
    }
}