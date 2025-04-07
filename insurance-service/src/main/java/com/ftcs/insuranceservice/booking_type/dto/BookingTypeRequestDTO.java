package com.ftcs.insuranceservice.booking_type.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingTypeRequestDTO {
    private String bookingTypeName;
}
