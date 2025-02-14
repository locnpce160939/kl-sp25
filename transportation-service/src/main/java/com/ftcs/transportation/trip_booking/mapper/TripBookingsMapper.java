package com.ftcs.transportation.trip_booking.mapper;

import com.ftcs.transportation.trip_booking.dto.TripBookingsDetailDTO;
import com.ftcs.transportation.trip_booking.model.TripBookings;

public class TripBookingsMapper {
    public static TripBookingsDetailDTO toDTO(TripBookings tripBookings) {
        if (tripBookings == null) {
            return null;
        }
        return TripBookingsDetailDTO.builder()
                .bookingId(tripBookings.getBookingId())
                .accountId(tripBookings.getAccountId())
                .bookingType(tripBookings.getBookingType())
                .bookingDate(tripBookings.getBookingDate())
                .pickupLocation(tripBookings.getPickupLocation())
                .dropoffLocation(tripBookings.getDropoffLocation())
                .startLocationAddress(tripBookings.getStartLocationAddress())
                .endLocationAddress(tripBookings.getEndLocationAddress())
                .capacity(tripBookings.getCapacity())
                .status(String.valueOf(tripBookings.getStatus()))
                .expirationDate(tripBookings.getExpirationDate())
                .totalDistance(tripBookings.getTotalDistance())
                .price(tripBookings.getPrice())
                .notes(tripBookings.getNotes())
                .createAt(tripBookings.getCreateAt())
                .updateAt(tripBookings.getUpdateAt())
                .build();
    }
}
