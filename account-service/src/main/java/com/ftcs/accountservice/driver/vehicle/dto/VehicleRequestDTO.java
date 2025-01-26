package com.ftcs.accountservice.driver.vehicle.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.Year;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleRequestDTO {

    private Integer vehicleId;

    @NotBlank(message = "License plate must not be blank")
    @Size(max = 20, message = "License plate must not exceed 20 characters")
    private String licensePlate;

    @NotBlank(message = "Vehicle type must not be blank")
    @Size(max = 50, message = "Vehicle type must not exceed 50 characters")
    private String vehicleType;

    @NotBlank(message = "Make must not be blank")
    @Size(max = 50, message = "Make must not exceed 50 characters")
    private String make;

    @NotBlank(message = "Model must not be blank")
    @Size(max = 50, message = "Model must not exceed 50 characters")
    private String model;

    @NotNull(message = "Year must not be null")
    @Min(value = 1886, message = "Year must be greater than or equal to 1886")
    private Integer year;

    @NotNull(message = "Capacity must not be null")
    @Positive(message = "Capacity must be a positive number")
    private Integer capacity;

    @NotBlank(message = "Dimensions must not be blank")
    @Size(max = 100, message = "Dimensions must not exceed 100 characters")
    private String dimensions;

    @NotBlank(message = "Insurance status must not be blank")
    @Size(max = 20, message = "Insurance status must not exceed 20 characters")
    private String insuranceStatus;

    @NotNull(message = "Registration expiry date must not be null")
    @Future(message = "Registration expiry date must be in the future")
    private LocalDateTime registrationExpiryDate;

    @AssertTrue(message = "Year must not exceed the current year")
    public boolean isYearValid() {
        if (year == null) {
            return true;
        }
        int currentYear = Year.now().getValue();
        return year <= currentYear;
    }
}
