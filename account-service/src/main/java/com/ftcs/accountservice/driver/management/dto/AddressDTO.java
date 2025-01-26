package com.ftcs.accountservice.driver.management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
public class AddressDTO {
    private Integer addressDriverId;
    private String streetAddress;
    private String wardName;
    private String districtName;
    private Integer provinceId;
    private String addressType;
    private String addressNotes;
}