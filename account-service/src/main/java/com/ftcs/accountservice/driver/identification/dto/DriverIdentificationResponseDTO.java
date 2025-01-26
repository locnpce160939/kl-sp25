package com.ftcs.accountservice.driver.identification.dto;

import com.ftcs.accountservice.driver.shared.StatusDocumentType;
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
    private String fullName;
    private String gender;
    private LocalDateTime birthday;
    private String country;
    private String idNumber;
    private StatusDocumentType status;
    private LocalDateTime issueDate;
    private LocalDateTime expiryDate;
    private String issuedBy;
    private String frontView;
    private String backView;
    private AddressDriverResponseDTO permanentAddress;
    private AddressDriverResponseDTO temporaryAddress;
}
