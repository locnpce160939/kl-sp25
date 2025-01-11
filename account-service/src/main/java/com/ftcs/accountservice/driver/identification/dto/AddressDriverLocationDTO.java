package com.ftcs.accountservice.driver.identification.dto;

import com.ftcs.common.feature.location.dto.LocationDto;
import com.ftcs.common.feature.location.dto.ReverseGeocodeResponseDto;
import lombok.*;

import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressDriverLocationDTO {
    Integer id;
    String name;
    List<LocationDto> templateLocations;
}
