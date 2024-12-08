package com.ftcs.transportation.trip_booking.controller;

import com.ftcs.accountservice.AccountURL;
import com.ftcs.common.dto.ApiResponse;
import com.ftcs.transportation.trip_booking.TripBookingsURL;
import com.ftcs.transportation.trip_booking.dto.FindTripBookingByTimePeriodRequestDTO;
import com.ftcs.transportation.trip_booking.dto.TripBookingsRequestDTO;
import com.ftcs.transportation.trip_booking.dto.UpdateStatusTripBookingsRequestDTO;
import com.ftcs.transportation.trip_booking.model.TripBookings;
import com.ftcs.transportation.trip_booking.service.TripBookingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(TripBookingsURL.TRIP_BOOKINGS)
public class TripBookingsController {

    private final TripBookingsService tripBookingsService;

    @PostMapping("/create")
    public ApiResponse<TripBookings> createTripBookings(@Valid @RequestBody TripBookingsRequestDTO tripBookingsRequestDTO,
                                                        @RequestAttribute("accountId") Integer accountId) {
        TripBookings tripBookings = tripBookingsService.createTripBookings(tripBookingsRequestDTO, accountId);
        return new ApiResponse<>(tripBookings);
    }

    @PutMapping("/update/{bookingId}")
    public ApiResponse<String> updateTripBookings(@Valid @RequestBody TripBookingsRequestDTO tripBookingsRequestDTO,
                                                  @PathVariable("bookingId") Integer bookingId) {
        tripBookingsService.updateTripBookings(tripBookingsRequestDTO, bookingId);
        return new ApiResponse<>("Trip booking updated successfully");
    }

    @PutMapping("/cancel/{bookingId}")
    public ApiResponse<String> cancelTripBookings(@Valid @PathVariable("bookingId") Integer bookingId) {
        tripBookingsService.cancelTripBookings(bookingId);
        return new ApiResponse<>("Trip booking cancelled successfully");
    }

    @GetMapping("/all")
    public ApiResponse<List<TripBookings>> getAllTripBookings() {
        List<TripBookings> tripBookings = tripBookingsService.getAllTripBookings();
        return new ApiResponse<>(tripBookings);
    }

    @GetMapping("/{bookingId}")
    public ApiResponse<TripBookings> getTripBookings(@PathVariable("bookingId") Integer bookingId) {
        TripBookings tripBookings = tripBookingsService.getTripBookings(bookingId);
        return new ApiResponse<>(tripBookings);
    }

    @PostMapping("/filter")
    public ApiResponse<List<TripBookings>> filterTripBookings(@Valid @RequestBody FindTripBookingByTimePeriodRequestDTO findTripBookingByTimePeriodRequestDTO) {
        List<TripBookings> tripBookings = tripBookingsService.filterTripBookings(findTripBookingByTimePeriodRequestDTO);
        return new ApiResponse<>(tripBookings);
    }

    @PutMapping("/updateStatusForDriver/{bookingId}")
    public ApiResponse<String> updateStatusForDriver(@Valid @RequestBody UpdateStatusTripBookingsRequestDTO updateStatusTripBookingsRequestDTO,
                                                     @RequestAttribute("accountId") Integer accountId,
                                                     @PathVariable("bookingId") Integer bookingId) {
        tripBookingsService.updateStatusForDriver(updateStatusTripBookingsRequestDTO, accountId, bookingId);
        return new ApiResponse<>("Driver status updated successfully");
    }

    @PutMapping("/continueFindingDriver/{bookingId}")
    public ApiResponse<String> continueFindingDriver(@Valid @RequestBody UpdateStatusTripBookingsRequestDTO updateStatusTripBookingsRequestDTO,
                                                     @PathVariable("bookingId") Integer bookingId) {
        tripBookingsService.continueFindingDriver(updateStatusTripBookingsRequestDTO, bookingId);
        return new ApiResponse<>("Continuing to find driver");
    }
}
