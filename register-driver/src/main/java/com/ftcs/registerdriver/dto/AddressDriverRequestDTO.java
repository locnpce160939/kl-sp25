package com.ftcs.registerdriver.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

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

    private String addressType;
}
