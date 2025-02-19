package com.ftcs.balanceservice.payment;

import com.ftcs.balanceservice.BalanceURL;
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

    @GetMapping
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<List<Payment>> getAllPayments() {
        return new ApiResponse<>(paymentService.getAllPayments());
    }

    @GetMapping("/{paymentId}")
    public ApiResponse<Payment> getPaymentById(@PathVariable("paymentId") Long paymentId) {
        return new ApiResponse<>(paymentService.getPaymentById(paymentId));
    }

    @PostMapping("/bookings/{bookingId}")
    public ApiResponse<Payment> createPayment(@PathVariable("bookingId") @Valid Long bookingId,
                                              @RequestAttribute("accountId") Integer accountId) {

        return new ApiResponse<>(paymentService.createPayment(bookingId, accountId));
    }

    @GetMapping("/qr/{paymentId}")
    public ApiResponse<String> generateQrCode(@PathVariable("paymentId") Long paymentId) {
        return new ApiResponse<>(paymentService.generateQrCode(paymentId));
    }

    @GetMapping("/verify/{paymentId}")
    public ApiResponse<String> verifyPayment(@PathVariable("paymentId") Long paymentId) {
        return new ApiResponse<>(paymentService.verifyPayment(paymentId));
    }
}
