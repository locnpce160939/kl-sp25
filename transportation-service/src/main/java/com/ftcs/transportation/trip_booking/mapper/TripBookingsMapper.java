package com.ftcs.transportation.trip_booking.mapper;

import com.ftcs.balanceservice.payment.model.Payment;
import com.ftcs.transportation.trip_booking.dto.TripBookingsDTO;
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
                .totalDistance(tripBookings.getTotalDistance())
                .price(tripBookings.getPrice())
                .notes(tripBookings.getNotes())
                .createAt(tripBookings.getCreateAt())
                .updateAt(tripBookings.getUpdateAt())
                .build();
    }

    public static TripBookingsDTO toTripBookingsDTO(TripBookings tripBookings, Payment payment) {
        if (tripBookings == null) {
            return TripBookingsDTO.builder().build();
        }

        return TripBookingsDTO.builder()
                .bookingId(tripBookings.getBookingId())
                .accountId(tripBookings.getAccountId())
                .tripAgreementId(tripBookings.getTripAgreementId())
                .bookingType(tripBookings.getBookingType())
                .bookingDate(tripBookings.getBookingDate())
                .pickupLocation(tripBookings.getPickupLocation())
                .dropoffLocation(tripBookings.getDropoffLocation())
                .startLocationAddress(tripBookings.getStartLocationAddress())
                .endLocationAddress(tripBookings.getEndLocationAddress())
                .capacity(tripBookings.getCapacity())
                .status(tripBookings.getStatus())
                .paymentMethod(tripBookings.getPaymentMethod())
                .totalDistance(tripBookings.getTotalDistance())
                .price(tripBookings.getPrice())
                .notes(tripBookings.getNotes())
                .createAt(tripBookings.getCreateAt())
                .updateAt(tripBookings.getUpdateAt())
                .payment(payment)
                .notes(tripBookings.getNotes())
                .originalPrice(tripBookings.getOriginalPrice())
                .discountAmount(tripBookings.getDiscountAmount())
                .insurancePrice(tripBookings.getInsurancePrice())
                .recipientPhoneNumber(tripBookings.getRecipientPhoneNumber())
                .build();
    }

    public static TripBookings toEntity(TripBookingsDTO dto) {
        if (dto == null) {
            return null;
        }

        return TripBookings.builder()
                .bookingId(dto.getBookingId())
                .accountId(dto.getAccountId())
                .tripAgreementId(dto.getTripAgreementId())
                .bookingType(dto.getBookingType())
                .bookingDate(dto.getBookingDate())
                .pickupLocation(dto.getPickupLocation())
                .dropoffLocation(dto.getDropoffLocation())
                .startLocationAddress(dto.getStartLocationAddress())
                .endLocationAddress(dto.getEndLocationAddress())
                .capacity(dto.getCapacity())
                .status(dto.getStatus())
                .paymentMethod(dto.getPaymentMethod())
                .totalDistance(dto.getTotalDistance())
                .price(dto.getPrice())
                .notes(dto.getNotes())
                .createAt(dto.getCreateAt())
                .updateAt(dto.getUpdateAt())
                .build();
    }
}
