package com.ftcs.transportation.trip_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PreviewTripBookingDTO {
    Double price;
    Double expectedDistance;
    Boolean isFirstOrder;
    List<PreviewInsuranceDTO> insurances;
}


