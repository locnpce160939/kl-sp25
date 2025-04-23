package com.ftcs.transportation.trip_booking.dto;

import com.ftcs.transportation.trip_booking.constant.PaymentMethod;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TripBookingsRequestDTO {
    private Long bookingType;

//    @NotNull(message = "Booking date cannot be null")
    private LocalDateTime bookingDate;

//    @NotBlank(message = "Pickup location cannot be null")
    private String pickupLocation;

//    @NotBlank(message = "Dropoff location cannot be null")
    private String dropoffLocation;

//    @NotBlank(message = "Start location cannot be blank")
    private String startLocationAddress;

//    @NotBlank(message = "End location cannot be blank")
    private String endLocationAddress;

    private Boolean useInsurance;

    private Long selectedInsurancePolicyId;

    private String notes;

    private String recipientPhoneNumber;
//    @NotNull(message = "Capacity cannot be null")
//    @Min(value = 1, message = "Capacity must be greater than or equal to 1")
    private Integer capacity;

//    @NotNull(message = "PaymentMethod cannot be null")
    private PaymentMethod paymentMethod;

//    @NotNull(message = "Expiration date cannot be null")
//    @Future(message = "Expiration date must be in the future")
    //private LocalDateTime expirationDate;

    private String voucherCode;
    private Long voucherId;
}
