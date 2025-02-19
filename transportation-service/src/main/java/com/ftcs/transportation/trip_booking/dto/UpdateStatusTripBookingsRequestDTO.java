package com.ftcs.transportation.trip_booking.dto;

import com.ftcs.transportation.trip_booking.constant.PaymentMethod;
import com.ftcs.transportation.trip_booking.constant.TripBookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusTripBookingsRequestDTO {
    private TripBookingStatus status;
    private String option;
    private PaymentMethod paymentMethod;

}
