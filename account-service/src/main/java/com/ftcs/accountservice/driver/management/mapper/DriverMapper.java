package com.ftcs.accountservice.driver.management.mapper;

import com.ftcs.accountservice.driver.management.dto.*;
import com.ftcs.accountservice.driver.management.projection.ListDriverProjection;
import com.ftcs.accountservice.driver.verification.service.VerificationDriverService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DriverMapper {
    public static List<ListDriverDTO> mapToListDriverDTO(List<ListDriverProjection> driverDetails, VerificationDriverService verificationDriverService) {
        Map<Integer, ListDriverDTO> driverMap = new HashMap<>();

        for (ListDriverProjection driver : driverDetails) {
            Integer accountId = driver.getAccountId();
            if (!driverMap.containsKey(accountId)) {
                ListDriverDTO driverDTO = new ListDriverDTO(
                        accountId,
                        driver.getUsername(),
                        driver.getEmail(),
                        driver.getPhone(),
                        driver.getRole(),
                        driver.getProfilePicture(),
                        driver.getLastLogin(),
                        verificationDriverService.checkRequiredInformation(accountId),
                        new DriverIdentityDTO(
                                driver.getDriverIdentificationId(),
                                driver.getDriverIDNumber(),
                                driver.getDriverFullName(),
                                driver.getDriverGender(),
                                driver.getDriverBirthday(),
                                driver.getDriverCountry(),
                                driver.getPermanentAddress(),
                                driver.getTemporaryAddress(),
                                driver.getDriverIDIssueDate(),
                                driver.getDriverIDExpiryDate(),
                                driver.getDriverIDIssuedBy(),
                                driver.getDriverIDStatus(),
                                driver.getDriverIDFrontView(),
                                driver.getDriverIDBackView()
                        ),
                        new LicenseDTO(
                                driver.getLicenseId(),
                                driver.getLicenseNumber(),
                                driver.getLicenseType(),
                                driver.getLicenseIssuedDate(),
                                driver.getLicenseExpiryDate(),
                                driver.getIssuingAuthority(),
                                driver.getLicenseStatus(),
                                driver.getLicenseFrontView(),
                                driver.getLicenseBackView()
                        ),

                        new ArrayList<>(),

                        // Nhóm thông tin Address
                        new ArrayList<>(),

                        driver.getAccountCreatedAt(),
                        driver.getAccountUpdatedAt()
                );
                driverMap.put(accountId, driverDTO);
            }

            // Thêm phương tiện vào tài xế
            DriverVehicleDTO vehicleDTO = new DriverVehicleDTO(
                    driver.getVehicleId(),
                    driver.getLicensePlate(),
                    driver.getVehicleType(),
                    driver.getVehicleMake(),
                    driver.getVehicleModel(),
                    driver.getVehicleYear(),
                    driver.getVehicleCapacity(),
                    driver.getVehicleDimensions(),
                    driver.getVehicleStatus(),
                    driver.getVehicleVerified(),
                    driver.getVehicleFrontView(),
                    driver.getVehicleBackView()
            );
            driverMap.get(accountId).getVehicles().add(vehicleDTO);

            // Thêm địa chỉ vào danh sách địa chỉ của tài xế
            AddressDTO addressDTO = new AddressDTO(
                    driver.getAddressDriverId(),
                    driver.getStreetAddress(),
                    driver.getWardName(),
                    driver.getDistrictName(),
                    driver.getProvinceId(),
                    driver.getAddressType(),
                    driver.getAddressNotes()
            );
            driverMap.get(accountId).getAddressList().add(addressDTO);
        }

        // Trả về danh sách tài xế sau khi đã nhóm theo accountId
        return new ArrayList<>(driverMap.values());
    }
}