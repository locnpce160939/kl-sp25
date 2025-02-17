package com.ftcs.payment.constant;

import com.ftcs.transportation.trip_matching.constant.PaymentStatusType;

import java.time.LocalDateTime;

@lombok.Data
@lombok.Builder
public class PaymentNotification {
    private Long paymentId;
    private Long bookingId;
    private double amount;
    private PaymentStatusType status;
    private String message;
    private LocalDateTime timestamp;
}
