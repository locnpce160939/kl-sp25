package com.ftcs.transportation.trip_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreviewInsuranceDTO {
    String insuranceName;
    String insuranceDescription;
    Double insurancePrice;
    Long insurancePolicyId;

}
