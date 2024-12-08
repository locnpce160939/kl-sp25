package com.ftcs.transportation.trip_booking.service;

import com.ftcs.common.exception.BadRequestException;
import com.ftcs.transportation.schelude.dto.FindScheduleByTimePeriodRequestDTO;
import com.ftcs.transportation.schelude.model.Schedule;
import com.ftcs.transportation.schelude.repository.ScheduleRepository;
import com.ftcs.transportation.trip_booking.dto.FindTripBookingByTimePeriodRequestDTO;
import com.ftcs.transportation.trip_booking.dto.TripBookingsRequestDTO;
import com.ftcs.transportation.trip_booking.dto.UpdateStatusTripBookingsRequestDTO;
import com.ftcs.transportation.trip_booking.model.TripBookings;
import com.ftcs.transportation.trip_booking.repository.TripBookingsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class TripBookingsService {

    private final TripBookingsRepository tripBookingsRepository;
    private final ScheduleRepository scheduleRepository;

    public TripBookings createTripBookings(TripBookingsRequestDTO tripBookingsRequestDTO, Integer accountId) {
        validateExpirationDate(tripBookingsRequestDTO);

        TripBookings tripBookings = new TripBookings();
        tripBookings.setAccountId(accountId);
        mapRequestToTripBookings(tripBookingsRequestDTO, tripBookings);
        return tripBookingsRepository.save(tripBookings);
    }

    public void updateTripBookings(TripBookingsRequestDTO tripBookingsRequestDTO, Integer bookingId) {
        TripBookings tripBookings = findTripBookingsById(bookingId);
        mapRequestToTripBookings(tripBookingsRequestDTO, tripBookings);
        tripBookingsRepository.save(tripBookings);
    }

    public void cancelTripBookings(Integer bookingId) {
        TripBookings tripBookings = findTripBookingsById(bookingId);
        validateCancellationStatus(tripBookings);
        tripBookings.setStatus("Cancelled");
        tripBookingsRepository.save(tripBookings);
    }

    public TripBookings getTripBookings(Integer bookingId) {
        return findTripBookingsById(bookingId);
    }

    public List<TripBookings> getAllTripBookings() {
        List<TripBookings> bookings = tripBookingsRepository.findAll();
        if (bookings.isEmpty()) {
            throw new BadRequestException("No trip bookings found");
        }
        return bookings;
    }

    public void updateStatusForDriver(UpdateStatusTripBookingsRequestDTO updateStatusTripBookingsRequestDTO, Integer accountId, Integer bookingId) {
        TripBookings tripBookings = findTripBookingsById(bookingId);
        if (tripBookings.getStatus().equals("Arranging driver")) {
            handleDriverStatusUpdate(updateStatusTripBookingsRequestDTO, accountId, tripBookings);
        }
    }

    public void continueFindingDriver(UpdateStatusTripBookingsRequestDTO updateStatusTripBookingsRequestDTO, Integer bookingId) {
        TripBookings tripBookings = findTripBookingsById(bookingId);
        if ("Cancelled".equals(tripBookings.getStatus()) &&
                "Confirmed".equals(updateStatusTripBookingsRequestDTO.getOption())) {
            tripBookings.setStatus("Arranging driver");
            tripBookingsRepository.save(tripBookings);
        }
    }

    public List<TripBookings> filterTripBookings(FindTripBookingByTimePeriodRequestDTO request) {
        boolean hasDateRange = request.getStartDate() != null && request.getEndDate() != null;
        boolean hasStatus = request.getStatus() != null && !request.getStatus().isEmpty();
        List<TripBookings> tripBookings;
        if (hasDateRange && hasStatus) {
            tripBookings = tripBookingsRepository.findAllByBookingDateBetweenAndStatus(
                    request.getStartDate(),
                    request.getEndDate(),
                    request.getStatus()
            );
        } else if (hasDateRange) {
            tripBookings = tripBookingsRepository.findAllByBookingDateBetween(
                    request.getStartDate(),
                    request.getEndDate()
            );
        } else if (hasStatus) {
            tripBookings = tripBookingsRepository.findAllByStatus(request.getStatus());
        } else {
            tripBookings = tripBookingsRepository.findAll();
        }

        if (tripBookings.isEmpty()) {
            throw new BadRequestException("No trip bookings found with the given criteria.");
        }
        return tripBookings;
    }

    private void validateExpirationDate(TripBookingsRequestDTO tripBookingsRequestDTO) {
        if (!tripBookingsRequestDTO.getExpirationDate().isAfter(tripBookingsRequestDTO.getBookingDate())) {
            throw new BadRequestException("Expiration date must be after booking date");
        }
    }

    private TripBookings findTripBookingsById(Integer bookingId) {
        return tripBookingsRepository.findTripBookingsByBookingId(bookingId)
                .orElseThrow(() -> new BadRequestException("Trip booking not found!"));
    }

    private TripBookings findTripBookingsByAccountId(Integer accountId) {
        return tripBookingsRepository.findTripBookingsByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("You haven't booked any trips yet!"));
    }

    private void validateCancellationStatus(TripBookings tripBookings) {
        if ("Driver is on the way".equals(tripBookings.getStatus())) {
            throw new BadRequestException("You can't cancel because the driver is on the way");
        }
    }

    private void handleDriverStatusUpdate(UpdateStatusTripBookingsRequestDTO updateStatusTripBookingsRequestDTO,
                                          Integer accountId, TripBookings tripBookings) {
        Schedule schedule = scheduleRepository.findScheduleByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("Schedule does not exist!"));
        if (!"Waiting for delivery".equals(schedule.getStatus())) {
            throw new BadRequestException("Schedule status must be 'Waiting for delivery' to proceed.");
        }
        if ("Confirmed".equals(updateStatusTripBookingsRequestDTO.getOption())) {
            tripBookings.setStatus("Driver is on the way");
            tripBookings.setScheduleId(schedule.getScheduleId());
            schedule.setStatus("Getting to the point");
            scheduleRepository.save(schedule);
        } else {
            tripBookings.setStatus("Cancelled");
        }

        tripBookingsRepository.save(tripBookings);
    }


    private void mapRequestToTripBookings(TripBookingsRequestDTO tripBookingsRequestDTO, TripBookings tripBookings) {
        tripBookings.setBookingType(tripBookingsRequestDTO.getBookingType());
        tripBookings.setBookingDate(tripBookingsRequestDTO.getBookingDate());
        tripBookings.setPickupLocation(tripBookingsRequestDTO.getPickupLocation());
        tripBookings.setDropoffLocation(tripBookingsRequestDTO.getDropoffLocation());
        tripBookings.setCapacity(tripBookingsRequestDTO.getCapacity());
        tripBookings.setExpirationDate(tripBookingsRequestDTO.getExpirationDate());
        tripBookings.setStatus("Arranging driver");
    }
}
