package com.ftcs.accountservice.driver.identification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverIdentificationResponseDTO {
    private Integer driverIdentificationId;
    private Integer accountId;
    private String idNumber;
    private String status;
    private LocalDateTime issueDate;
    private LocalDateTime expiryDate;
    private String issuedBy;
    private Boolean isVerified;
    private AddressDriverResponseDTO permanentAddress;
    private AddressDriverResponseDTO temporaryAddress;
}
