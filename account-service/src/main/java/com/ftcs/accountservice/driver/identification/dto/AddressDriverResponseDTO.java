package com.ftcs.accountservice.driver.identification.dto;

import com.ftcs.common.dto.IdAndName;
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
    private AddressDriverLocationDTO ward;
    private AddressDriverLocationDTO district;
    private AddressDriverLocationDTO province;
    private String addressType;
}
