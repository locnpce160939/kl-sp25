package com.ftcs.accountservice.driver.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class LicenseDTO {
    private String licenseNumber;
    private String licenseType;
    private LocalDateTime licenseIssuedDate;
    private LocalDateTime licenseExpiryDate;
    private String issuingAuthority;
    private String licenseStatus;
}