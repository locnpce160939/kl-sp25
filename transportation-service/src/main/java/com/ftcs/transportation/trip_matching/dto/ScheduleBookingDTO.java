package com.ftcs.transportation.trip_matching.dto;

import com.ftcs.transportation.schedule.model.Schedule;
import com.ftcs.transportation.trip_booking.model.TripBookings;
import lombok.*;

@Getter
@Setter
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleBookingDTO {
    private Schedule schedule;
    private TripBookings tripBooking;
}
