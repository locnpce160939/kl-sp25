package com.ftcs.balanceservice.payment;

import com.ftcs.balanceservice.BalanceURL;
import com.ftcs.balanceservice.payment.service.PaymentVerificationService;
import com.ftcs.common.dto.ApiResponse;
import com.ftcs.balanceservice.payment.model.Payment;
import com.ftcs.balanceservice.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(BalanceURL.PAYMENT)
public class PaymentController {
    private final PaymentService paymentService;
    private final PaymentVerificationService paymentVerificationService;

    @GetMapping
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<List<Payment>> getAllPayments() {
        return new ApiResponse<>(paymentService.getAllPayments());
    }

    @GetMapping("/{paymentId}")
    public ApiResponse<Payment> getPaymentById(@PathVariable("paymentId") Long paymentId) {
        return new ApiResponse<>(paymentService.getPaymentById(paymentId));
    }

    @GetMapping("/tripBooking/{tripBookingId}")
    public ApiResponse<Payment> getPaymentByTripBooking(@PathVariable("tripBookingId") Long tripBookingId) {
        return new ApiResponse<>(paymentService.getPaymentByTripBookingId(tripBookingId));
    }
}
