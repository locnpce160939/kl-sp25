package com.ftcs.transportation.trip_matching.mapper;

import com.ftcs.transportation.schedule.model.Schedule;
import com.ftcs.transportation.trip_booking.model.TripBookings;
import com.ftcs.transportation.trip_matching.projection.ScheduleBookingProjection;
import com.ftcs.transportation.trip_matching.dto.ScheduleBookingDTO;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ScheduleBookingMapper {
    public static ScheduleBookingDTO mapToDTO(ScheduleBookingProjection projection) {
        Schedule schedule = mapToSchedule(projection);
        TripBookings tripBooking = mapToTripBooking(projection);
        return new ScheduleBookingDTO(schedule, tripBooking);
    }

    private static Schedule mapToSchedule(ScheduleBookingProjection projection) {
        Schedule schedule = new Schedule();
        schedule.setScheduleId(projection.getScheduleId());
        schedule.setAccountId(projection.getAccountId());
        schedule.setStartLocation(projection.getStartLocation());
        schedule.setEndLocation(projection.getEndLocation());
        schedule.setStartLocationAddress(projection.getStartLocationAddress());
        schedule.setEndLocationAddress(projection.getEndLocationAddress());
        schedule.setStartDate(projection.getStartDate());
        schedule.setAvailableCapacity(projection.getAvailableCapacity());
        schedule.setStatus(projection.getScheduleStatus());
        schedule.setNotes(projection.getScheduleNotes());
        schedule.setCreateAt(projection.getScheduleCreateAt());
        schedule.setUpdateAt(projection.getScheduleUpdateAt());
        return schedule;
    }

    private static TripBookings mapToTripBooking(ScheduleBookingProjection projection) {
        TripBookings tripBooking = new TripBookings();
        tripBooking.setBookingId(projection.getBookingId());
        tripBooking.setAccountId(projection.getBookingAccountId());
        tripBooking.setBookingType(projection.getBookingType());
        tripBooking.setBookingDate(projection.getBookingDate());
        tripBooking.setPickupLocation(projection.getPickupLocation());
        tripBooking.setDropoffLocation(projection.getDropoffLocation());
        tripBooking.setStartLocationAddress(projection.getBookingStartLocationAddress());
        tripBooking.setEndLocationAddress(projection.getBookingEndLocationAddress());
        tripBooking.setCapacity(projection.getBookingCapacity());
        tripBooking.setStatus(projection.getBookingStatus());
        //tripBooking.setTotalPrice(projection.getTotalPrice());
        tripBooking.setNotes(projection.getBookingNotes());
        tripBooking.setCreateAt(projection.getBookingCreateAt());
        tripBooking.setUpdateAt(projection.getBookingUpdateAt());
        return tripBooking;
    }
}
