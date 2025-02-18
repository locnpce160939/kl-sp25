package com.ftcs.payment.dto;

import com.ftcs.transportation.trip_matching.constant.PaymentStatusType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentRequestDTO {
    private Long bookingId;
    private Double amount;
    private PaymentStatusType paymentStatus;
    private LocalDateTime paymentDate;
    private Long transactionId;
}
