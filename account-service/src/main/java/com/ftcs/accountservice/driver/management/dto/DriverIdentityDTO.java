package com.ftcs.accountservice.driver.management.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class DriverIdentityDTO {
    private Integer driverIdentificationId;
    private String driverIDNumber;
    private String driverFullName;
    private String driverGender;
    private LocalDateTime driverBirthday;
    private String driverCountry;
    private String permanentAddress;
    private String temporaryAddress;
    private LocalDateTime driverIDIssueDate;
    private LocalDateTime driverIDExpiryDate;
    private String driverIDIssuedBy;
    private String driverIDStatus;
    private String driverFrontView;
    private String driverBackView;
}
