package com.ftcs.payment;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.payment.dto.PaymentRequestDTO;
import com.ftcs.payment.model.Payment;
import com.ftcs.payment.service.PaymentService;
import com.ftcs.transportation.TransportationURL;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(PaymentURL.PAYMENT)
public class PaymentController {
    private final PaymentService paymentService;

    @GetMapping()
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<List<Payment>> getAllPayments() {
        return new ApiResponse<>(paymentService.getAllPayments());
    }

    @GetMapping("/{paymentId}")
    public ApiResponse<Payment> getPaymentById(@PathVariable("paymentId") Long paymentId) {
        return new ApiResponse<>(paymentService.getPaymentById(paymentId));
    }

    @PostMapping("/create/{tripBookingId}")
    public ApiResponse<?> createPayment(@Valid
                                              @PathVariable("tripBookingId") Long tripBookingId) {
        paymentService.createPayment(tripBookingId);
        return new ApiResponse<>("Created payment successfully");
    }

    @GetMapping("/qr/{paymentId}")
    public ApiResponse<?> getQrBank(@PathVariable("paymentId") Long paymentId) {
        return new ApiResponse<>(paymentService.getQrBank(paymentId));
    }

    @GetMapping("/check/{paymentId}")
    public ApiResponse<String> checkPaymentStatus(@PathVariable("paymentId") Long paymentId) {
        return new ApiResponse<>(paymentService.checkTransactionStatus(paymentId));
    }
}
