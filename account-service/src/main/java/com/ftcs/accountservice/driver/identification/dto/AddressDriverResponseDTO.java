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
    private String wardName;
    private String districtName;
    private String provinceName;
    private String addressType;
}
