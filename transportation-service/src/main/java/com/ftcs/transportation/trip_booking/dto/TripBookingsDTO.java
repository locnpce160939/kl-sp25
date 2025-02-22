package com.ftcs.transportation.trip_booking.dto;

import com.ftcs.balanceservice.payment.model.Payment;
import com.ftcs.transportation.trip_booking.constant.PaymentMethod;
import com.ftcs.transportation.trip_booking.constant.TripBookingStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripBookingsDTO {
    private Long bookingId;
    private Integer accountId;
    private Long tripAgreementId;
    private String bookingType;
    private LocalDateTime bookingDate;
    private String pickupLocation;
    private String dropoffLocation;
    private String startLocationAddress;
    private String endLocationAddress;
    private Integer capacity;
    private TripBookingStatus status;
    private PaymentMethod paymentMethod;
    private LocalDateTime expirationDate;
    private Double totalDistance;
    private Double price;
    private String notes;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
    private Payment payment;
}
