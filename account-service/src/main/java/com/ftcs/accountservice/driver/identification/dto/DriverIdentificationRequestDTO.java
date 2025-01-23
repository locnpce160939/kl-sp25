package com.ftcs.accountservice.driver.identification.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverIdentificationRequestDTO {

    @NotBlank(message = "ID Number must not be null or empty")
    private String idNumber;

    @NotBlank(message = "Full name must not be null or empty")
    private String fullName;

    @NotBlank(message = "Gender must not be null or empty")
    private String gender;

    private LocalDateTime birthday;

    @NotBlank(message = "Country must not be null or empty")
    private String country;

    @NotNull(message = "Permanent Address Ward must not be null")
    private Integer permanentAddressWard;

    @NotNull(message = "Permanent Address District must not be null")
    private Integer permanentAddressDistrict;

    @NotNull(message = "Permanent Address Province must not be null")
    private Integer permanentAddressProvince;

    @NotBlank(message = "Permanent Street Address must not be null or empty")
    private String permanentStreetAddress;

    @NotNull(message = "Temporary Address Ward must not be null")
    private Integer temporaryAddressWard;

    @NotNull(message = "Temporary Address District must not be null")
    private Integer temporaryAddressDistrict;

    @NotNull(message = "Temporary Address Province must not be null")
    private Integer temporaryAddressProvince;

    @NotBlank(message = "Temporary Street Address must not be null or empty")
    private String temporaryStreetAddress;

    @NotNull(message = "Issue date must not be null")
    private LocalDateTime issueDate;

    @NotNull(message = "Expiry date must not be null")
    private LocalDateTime expiryDate;

    @NotBlank(message = "Issued by must not be null or empty")
    private String issuedBy;
}
