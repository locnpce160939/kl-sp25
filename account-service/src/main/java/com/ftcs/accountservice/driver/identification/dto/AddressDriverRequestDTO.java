package com.ftcs.accountservice.driver.identification.dto;

import com.ftcs.accountservice.driver.shared.AddressType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDriverRequestDTO {
    @NotNull(message = "Ward ID must not be null")
    private Integer wardId;

    @NotNull(message = "District ID must not be null")
    private Integer districtId;

    @NotNull(message = "Province ID must not be null")
    private Integer provinceId;

    @NotBlank(message = "Street Address must not be null or empty")
    private String streetAddress;

    private AddressType addressType;
}
