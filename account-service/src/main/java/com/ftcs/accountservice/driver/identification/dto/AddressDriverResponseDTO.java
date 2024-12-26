package com.ftcs.accountservice.driver.identification.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDriverResponseDTO {
    private Integer addressDriverId;
    private String streetAddress;
    private Integer wardId;
    private Integer districtId;
    private Integer provinceId;
    private String addressType;
}
