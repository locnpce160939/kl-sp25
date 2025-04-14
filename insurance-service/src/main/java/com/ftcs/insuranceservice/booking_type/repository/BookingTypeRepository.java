package com.ftcs.insuranceservice.booking_type.repository;

import com.ftcs.insuranceservice.booking_type.model.BookingType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BookingTypeRepository extends JpaRepository<BookingType, Long> {
    Optional<BookingType> findByBookingTypeId(Long bookingTypeId);
    Boolean existsByBookingTypeName(String bookingNumber);
}
