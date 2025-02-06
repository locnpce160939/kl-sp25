package com.ftcs.accountservice.driver.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ListDriverDTO {
    private Integer accountId;
    private String username;
    private String email;
    private String phone;
    private String role;
    private String profilePicture;
    private LocalDateTime lastLogin;
    private boolean accountStatus;

    // Information about driver identity (grouped into DriverIdentityDTO)
    private DriverIdentityDTO driverIdentity;

    // Information about license (grouped into LicenseDTO)
    private LicenseDTO license;

    // Vehicles of the driver
    private List<DriverVehicleDTO> vehicles; // List of vehicles

    // Driver address information (grouped into AddressDTO)
    private List<AddressDTO> addressList; // Permanent and temporary addresses

    // Account creation and update time
    private LocalDateTime accountCreatedAt;
    private LocalDateTime accountUpdatedAt;
}
