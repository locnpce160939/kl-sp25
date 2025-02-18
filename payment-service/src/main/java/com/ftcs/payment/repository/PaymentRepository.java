package com.ftcs.payment.repository;

import com.ftcs.payment.model.Payment;
import com.ftcs.transportation.trip_booking.constant.PaymentMethod;
import com.ftcs.transportation.trip_matching.constant.PaymentStatusType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findPaymentByPaymentStatus(PaymentStatusType status);
}
