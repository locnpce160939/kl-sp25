package com.ftcs.accountservice.customer.address.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDTO {
    @NotNull(message = "Ward ID must not be null")
    private Integer wardId;

    @NotNull(message = "District ID must not be null")
    private Integer districtId;

    @NotNull(message = "Province ID must not be null")
    private Integer provinceId;

    @NotBlank(message = "Street Address must not be null or empty")
    private String streetAddress;

    @NotBlank(message = "Address Type must not be null or empty")
    private String addressType;
}
