package com.ftcs.accountservice.driver.verification.dto;

import com.ftcs.accountservice.driver.shared.StatusDocumentType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifiedDocumentRequestDTO {
    private StatusDocumentType licenseVerified;
    private StatusDocumentType vehicleVerified;
    private StatusDocumentType driverIdentificationVerified;
}
