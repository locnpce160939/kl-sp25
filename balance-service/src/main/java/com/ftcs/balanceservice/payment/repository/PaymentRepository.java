package com.ftcs.balanceservice.payment.repository;

import com.ftcs.balanceservice.payment.constant.PaymentStatus;
import com.ftcs.balanceservice.payment.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findPaymentByPaymentStatus(PaymentStatus status);
    List<Payment> findPaymentByAccountId(Integer accountId);
    Optional<Payment> findPaymentByBookingId(Long bookingId);
}
