package com.ftcs.transportation.trip_booking.dto;

import com.ftcs.authservice.features.account.Account;
import com.ftcs.transportation.trip_agreement.model.TripAgreement;
import com.ftcs.transportation.trip_booking.model.TripBookings;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class TripBookingsDetailDTO {
    private Long bookingId;
    private Integer accountId;
    private TripAgreement tripAgreement;
    private Account driver;
    private Account customer;
    private Long bookingType;
    private LocalDateTime bookingDate;
    private String pickupLocation;
    private String dropoffLocation;
    private String startLocationAddress;
    private String endLocationAddress;
    private Integer capacity;
    private String status;
    private LocalDateTime expirationDate;
    private Double totalDistance;
    private Double price;
    private String notes;
    private LocalDateTime createAt;
    private LocalDateTime updateAt;
}