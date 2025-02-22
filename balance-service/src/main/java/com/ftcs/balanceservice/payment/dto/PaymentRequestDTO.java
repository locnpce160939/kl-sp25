package com.ftcs.balanceservice.payment.dto;

import com.ftcs.balanceservice.payment.constant.PaymentStatus;
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
    private PaymentStatus paymentStatus;
    private LocalDateTime paymentDate;
    private Long transactionId;
}
