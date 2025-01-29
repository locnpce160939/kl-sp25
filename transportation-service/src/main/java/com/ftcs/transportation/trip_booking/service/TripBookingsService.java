package com.ftcs.transportation.trip_booking.service;

import com.ftcs.common.exception.BadRequestException;
import com.ftcs.transportation.schelude.model.Schedule;
import com.ftcs.transportation.schelude.repository.ScheduleRepository;
import com.ftcs.transportation.trip_booking.dto.FindTripBookingByTimePeriodRequestDTO;
import com.ftcs.transportation.trip_booking.dto.TripBookingsRequestDTO;
import com.ftcs.transportation.trip_booking.dto.UpdateStatusTripBookingsRequestDTO;
import com.ftcs.transportation.trip_booking.model.TripBookings;
import com.ftcs.transportation.trip_booking.repository.TripBookingsRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@AllArgsConstructor
public class TripBookingsService {

    private final TripBookingsRepository tripBookingsRepository;
    private final ScheduleRepository scheduleRepository;

    public TripBookings createTripBookings(TripBookingsRequestDTO requestDTO, Integer accountId) {
        validateExpirationDate(requestDTO);
        TripBookings tripBookings = new TripBookings();
        tripBookings.setAccountId(accountId);
        mapRequestToTripBookings(requestDTO, tripBookings);
        return tripBookingsRepository.save(tripBookings);
    }

    public void updateTripBookings(TripBookingsRequestDTO requestDTO, Integer bookingId) {
        validateExpirationDate(requestDTO);
        TripBookings tripBookings = findTripBookingsById(bookingId);
        mapRequestToTripBookings(requestDTO, tripBookings);
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

    public void updateStatusForDriver(UpdateStatusTripBookingsRequestDTO requestDTO, Integer accountId, Integer bookingId) {
        TripBookings tripBookings = findTripBookingsById(bookingId);
        if (tripBookings.getStatus().equals("Arranging driver")) {
            handleDriverStatusUpdate(requestDTO, accountId, tripBookings);
        }else{
            throw new BadRequestException("Trip bookings status is not arranged");
        }
    }

    public void continueFindingDriver(UpdateStatusTripBookingsRequestDTO requestDTO, Integer bookingId) {
        TripBookings tripBookings = findTripBookingsById(bookingId);
        if ("Cancelled".equals(tripBookings.getStatus()) &&
                "Confirmed".equals(requestDTO.getOption())) {
            tripBookings.setStatus("Arranging driver");
            tripBookingsRepository.save(tripBookings);
        }
    }

    public List<TripBookings> filterTripBookings(FindTripBookingByTimePeriodRequestDTO requestDTO) {
        boolean hasDateRange = requestDTO.getStartDate() != null && requestDTO.getEndDate() != null;
        boolean hasStatus = requestDTO.getStatus() != null && !requestDTO.getStatus().isEmpty();
        List<TripBookings> tripBookings;
        if (hasDateRange && hasStatus) {
            tripBookings = tripBookingsRepository.findAllByBookingDateBetweenAndStatus(
                    requestDTO.getStartDate(),
                    requestDTO.getEndDate(),
                    requestDTO.getStatus()
            );
        } else if (hasDateRange) {
            tripBookings = tripBookingsRepository.findAllByBookingDateBetween(
                    requestDTO.getStartDate(),
                    requestDTO.getEndDate()
            );
        } else if (hasStatus) {
            tripBookings = tripBookingsRepository.findAllByStatus(requestDTO.getStatus());
        } else {
            tripBookings = tripBookingsRepository.findAll();
        }
        if (tripBookings.isEmpty()) {
            throw new BadRequestException("No trip bookings found with the given criteria.");
        }
        return tripBookings;
    }

    public void confirmCompleteDelivery(UpdateStatusTripBookingsRequestDTO requestDTO, String role, Integer bookingId) {
        TripBookings tripBookings = findTripBookingsById(bookingId);
        if (isDriverConfirmingDelivery(role, tripBookings)) {
            updateBookingStatus(tripBookings, "Delivered");
        } else if (isCustomerConfirmingCompletion(role, tripBookings, requestDTO)) {
            completeOrder(tripBookings);
        }
    }

    public List<TripBookings> getTripBookingsByAccountId(Integer accountId) {
        return tripBookingsRepository.findAllByAccountId(accountId);
    }

    public List<TripBookings> getTripBookingsByAccountIdOfAdminRole(Integer accountId) {
        return tripBookingsRepository.findAllByAccountId(accountId);
    }

    private boolean isDriverConfirmingDelivery(String role, TripBookings tripBookings) {
        return "DRIVER".equals(role) && "Delivered".equals(tripBookings.getStatus());
    }

    private boolean isCustomerConfirmingCompletion(String role, TripBookings tripBookings, UpdateStatusTripBookingsRequestDTO requestDTO) {
        return "CUSTOMER".equals(role)
                && "Delivered".equals(tripBookings.getStatus())
                && "Received the item".equals(requestDTO.getStatus());
    }

    private void updateBookingStatus(TripBookings tripBookings, String status) {
        tripBookings.setStatus(status);
        tripBookingsRepository.save(tripBookings);
    }

    private void completeOrder(TripBookings tripBookings) {
        updateBookingStatus(tripBookings, "Order completed");
        Schedule schedule = findScheduleByScheduleId(tripBookings.getScheduleId());
        schedule.setStatus("This schedule is complete!");
        scheduleRepository.save(schedule);
    }


    private void validateExpirationDate(TripBookingsRequestDTO requestDTO) {
        if (!requestDTO.getExpirationDate().isAfter(requestDTO.getBookingDate())) {
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

    private Schedule findScheduleByScheduleId(Integer scheduleId) {
        return scheduleRepository.findScheduleByScheduleId(scheduleId)
                .orElseThrow(() -> new BadRequestException("Schedule not found!"));
    }

    private void validateCancellationStatus(TripBookings tripBookings) {
        if ("Driver is on the way".equals(tripBookings.getStatus())) {
            throw new BadRequestException("You can't cancel because the driver is on the way");
        }
    }

    private void handleDriverStatusUpdate(UpdateStatusTripBookingsRequestDTO requestDTO,
                                          Integer accountId, TripBookings tripBookings) {
        Schedule schedule = scheduleRepository.findScheduleByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("Schedule does not exist!"));
        if (!"Waiting for delivery".equals(schedule.getStatus())) {
            throw new BadRequestException("Schedule status must be 'Waiting for delivery' to proceed.");
        }
        if ("Confirmed".equals(requestDTO.getOption())) {
            tripBookings.setStatus("Driver is on the way");
            tripBookings.setScheduleId(schedule.getScheduleId());
            schedule.setStatus("Getting to the point");
            scheduleRepository.save(schedule);
        } else {
            tripBookings.setStatus("Cancelled");
        }

        tripBookingsRepository.save(tripBookings);
    }

    private void mapRequestToTripBookings(TripBookingsRequestDTO requestDTO, TripBookings tripBookings) {
        tripBookings.setBookingType(requestDTO.getBookingType());
        tripBookings.setBookingDate(requestDTO.getBookingDate());
        tripBookings.setPickupLocation(requestDTO.getPickupLocation());
        tripBookings.setDropoffLocation(requestDTO.getDropoffLocation());
        tripBookings.setCapacity(requestDTO.getCapacity());
        tripBookings.setExpirationDate(requestDTO.getExpirationDate());
        tripBookings.setStartLocationAddress(requestDTO.getStartLocationAddress());
        tripBookings.setEndLocationAddress(requestDTO.getEndLocationAddress());
        tripBookings.setStatus("Arranging driver");
    }
}
