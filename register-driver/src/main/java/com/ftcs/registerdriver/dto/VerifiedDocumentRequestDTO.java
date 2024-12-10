package com.ftcs.registerdriver.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifiedDocumentRequestDTO {
    private Boolean licenseVerified;
    private Boolean vehicleVerified;
    private Boolean driverIdentificationVerified;
    private String status;

}
