package com.ftcs.balanceservice.payment.service;

import com.ftcs.balanceservice.payment.constant.PaymentStatus;
import com.ftcs.balanceservice.payment.model.Payment;
import com.ftcs.balanceservice.payment.repository.PaymentRepository;
import com.ftcs.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@EnableScheduling
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final QrCodeService qrCodeService;

    public Page<Payment> getAllPayments(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return paymentRepository.findAll(pageable);
    }

        public Payment getPaymentById(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BadRequestException("Payment not found with ID: " + paymentId));
    }

    public Payment getPaymentByTripBookingId(Long tripBookingId) {
        return paymentRepository.findPaymentByBookingId(tripBookingId)
                .orElseThrow(() -> new BadRequestException("Payment not found with Trip: " + tripBookingId));
    }

    public Payment createPayment(Long bookingId, Double price, Integer accountId) {
        Payment payment = Payment.builder()
                .amount(price)
                .accountId(accountId)
                .bookingId(bookingId)
                .paymentDate(LocalDateTime.now())
                .paymentStatus(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);
        String qrCode = qrCodeService.generateQrCode(payment);
        payment.setQrData(qrCode);

        paymentRepository.save(payment);

        return payment;
    }
}

