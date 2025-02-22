package com.ftcs.balanceservice.payment.constant;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class PaymentNotification {
    private Long paymentId;
    private Long bookingId;
    private double amount;
    private PaymentStatus status;
    private String message;
    private LocalDateTime timestamp;
}
