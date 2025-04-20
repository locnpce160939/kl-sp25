package com.ftcs.accountservice.customer.address.service;

import com.ftcs.accountservice.customer.address.dto.AddressRequestDTO;
import com.ftcs.accountservice.customer.address.model.Address;
import com.ftcs.accountservice.customer.address.repository.AddressRepository;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.common.feature.location.model.District;
import com.ftcs.common.feature.location.model.Ward;
import com.ftcs.common.feature.location.repository.DistrictRepository;
import com.ftcs.common.feature.location.repository.ProvinceRepository;
import com.ftcs.common.feature.location.repository.WardRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class AddressService {
    private final AddressRepository addressRepository;
    private final ProvinceRepository provinceRepository;
    private final DistrictRepository districtRepository;
    private final WardRepository wardRepository;

    public void createAddressCustomer(AddressRequestDTO requestDTO, Integer accountId){
        validateAddressCodes(requestDTO.getWardId(), requestDTO.getDistrictId(), requestDTO.getProvinceId());
        boolean address = addressRepository.existsAddressByAccountId(accountId);
        Address newAddress = Address.builder()
                .accountId(accountId)
                .streetAddress(requestDTO.getStreetAddress())
                .wardId(requestDTO.getWardId())
                .districtId(requestDTO.getDistrictId())
                .provinceId(requestDTO.getProvinceId())
                .addressType(requestDTO.getAddressType())
                .isDefault(!address)
                .build();
        addressRepository.save(newAddress);
    }

    public void updateAddressCustomer(Integer addressId, AddressRequestDTO requestDTO, Integer accountId) {
        Address existingAddress = findAddressByAddressId(addressId);
        validateAccountOwnership(accountId, existingAddress);
        validateAddressCodes(requestDTO.getWardId(), requestDTO.getDistrictId(), requestDTO.getProvinceId());
        existingAddress.setStreetAddress(requestDTO.getStreetAddress());
        existingAddress.setWardId(requestDTO.getWardId());
        existingAddress.setDistrictId(requestDTO.getDistrictId());
        existingAddress.setProvinceId(requestDTO.getProvinceId());
        existingAddress.setAddressType(requestDTO.getAddressType());
        addressRepository.save(existingAddress);
    }

    public void deleteAddressCustomer(Integer addressId, Integer accountId) {
        Address existingAddress = findAddressByAddressId(addressId);
        validateAccountOwnership(accountId, existingAddress);
        addressRepository.delete(existingAddress);
    }

    public void setDefaultAddress(Integer addressId, Integer accountId) {
        Address existingAddress = findAddressByAddressId(addressId);
        validateAccountOwnership(accountId, existingAddress);
        addressRepository.findAddressByAccountId(accountId).forEach(address -> {
            address.setIsDefault(false);
            addressRepository.save(address);
        });
        existingAddress.setIsDefault(true);
        addressRepository.save(existingAddress);
    }

    public List<Address> getAllAddressByAccountId(Integer accountId){
        return addressRepository.findAddressByAccountId(accountId);
    }

    private Address findAddressByAddressId(Integer addressId) {
        return addressRepository.findAddressByAddressId(addressId)
                .orElseThrow(() -> new BadRequestException("Address not found with ID: " + addressId));
    }

    private void validateAccountOwnership(Integer accountId, Address address) {
        if (!address.getAccountId().equals(accountId)) {
            throw new BadRequestException("This address does not belong to the specified account.");
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
