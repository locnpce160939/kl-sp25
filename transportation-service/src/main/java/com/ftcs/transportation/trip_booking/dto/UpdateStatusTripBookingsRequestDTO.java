package com.ftcs.transportation.trip_booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusTripBookingsRequestDTO {
    private String status;
    private String option;

}
