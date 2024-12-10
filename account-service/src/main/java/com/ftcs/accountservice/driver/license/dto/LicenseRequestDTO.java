package com.ftcs.accountservice.driver.license.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LicenseRequestDTO {

    @NotBlank(message = "License number must not be null or empty")
    @Size(max = 50, message = "License number must not exceed 50 characters")
    private String licenseNumber;

    @NotBlank(message = "License type must not be null or empty")
    @Size(max = 50, message = "License type must not exceed 50 characters")
    private String licenseType;

    @NotNull(message = "Issued date must not be null")
    @PastOrPresent(message = "Issued date must be in the past or present")
    private LocalDateTime issuedDate;

    @NotNull(message = "Expiry date must not be null")
    private LocalDateTime expiryDate;

    @NotBlank(message = "Issuing authority must not be null or empty")
    @Size(max = 100, message = "Issuing authority must not exceed 100 characters")
    private String issuingAuthority;
}
