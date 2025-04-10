package com.ftcs.insuranceservice.booking_insurance.repository;

import com.ftcs.insuranceservice.booking_insurance.model.BookingInsurance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingInsuranceRepository extends JpaRepository<BookingInsurance, Long> {
    Optional<BookingInsurance> findBookingInsuranceById(Long id);
    Page<BookingInsurance> findAllByAccountId(Integer accountId, Pageable pageable);
    Optional<BookingInsurance> findByBookingId(Long bookingId);
}
