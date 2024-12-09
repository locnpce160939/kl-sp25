package com.ftcs.transportation.trip_booking.controller;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.transportation.TransportationURL;
import com.ftcs.transportation.trip_booking.dto.FindTripBookingByTimePeriodRequestDTO;
import com.ftcs.transportation.trip_booking.dto.TripBookingsRequestDTO;
import com.ftcs.transportation.trip_booking.dto.UpdateStatusTripBookingsRequestDTO;
import com.ftcs.transportation.trip_booking.model.TripBookings;
import com.ftcs.transportation.trip_booking.service.TripBookingsService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(TransportationURL.TRIP_BOOKINGS)
public class TripBookingsController {

    private final TripBookingsService tripBookingsService;

    @PostMapping("/create")
    @PreAuthorize("hasPermission(null, 'CUSTOMER')")
    public ApiResponse<TripBookings> createTripBookings(@Valid @RequestBody TripBookingsRequestDTO requestDTO,
                                                        @RequestAttribute("accountId") Integer accountId) {
        TripBookings tripBookings = tripBookingsService.createTripBookings(requestDTO, accountId);
        return new ApiResponse<>(tripBookings);
    }

    @PutMapping("/update/{bookingId}")
    @PreAuthorize("hasPermission(null, 'CUSTOMER')")
    public ApiResponse<String> updateTripBookings(@Valid @RequestBody TripBookingsRequestDTO requestDTO,
                                                  @PathVariable("bookingId") Integer bookingId) {
        tripBookingsService.updateTripBookings(requestDTO, bookingId);
        return new ApiResponse<>("Trip booking updated successfully");
    }

    @PutMapping("/cancel/{bookingId}")
    @PreAuthorize("hasPermission(null, 'CUSTOMER')")
    public ApiResponse<String> cancelTripBookings(@Valid @PathVariable("bookingId") Integer bookingId) {
        tripBookingsService.cancelTripBookings(bookingId);
        return new ApiResponse<>("Trip booking cancelled successfully");
    }

    @GetMapping("/all")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
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
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<List<TripBookings>> filterTripBookings(@Valid @RequestBody FindTripBookingByTimePeriodRequestDTO requestDTO) {
        List<TripBookings> tripBookings = tripBookingsService.filterTripBookings(requestDTO);
        return new ApiResponse<>(tripBookings);
    }

    @PutMapping("/updateStatusForDriver/{bookingId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<String> updateStatusForDriver(@Valid @RequestBody UpdateStatusTripBookingsRequestDTO requestDTO,
                                                     @RequestAttribute("accountId") Integer accountId,
                                                     @PathVariable("bookingId") Integer bookingId) {
        tripBookingsService.updateStatusForDriver(requestDTO, accountId, bookingId);
        return new ApiResponse<>("Driver status updated successfully");
    }

    @PutMapping("/continueFindingDriver/{bookingId}")
    @PreAuthorize("hasPermission(null, 'CUSTOMER')")
    public ApiResponse<String> continueFindingDriver(@Valid @RequestBody UpdateStatusTripBookingsRequestDTO requestDTO,
                                                     @PathVariable("bookingId") Integer bookingId) {
        tripBookingsService.continueFindingDriver(requestDTO, bookingId);
        return new ApiResponse<>("Continuing to find driver");
    }

    @PutMapping("/confirmCompleteDelivery/{bookingId}")
    @PreAuthorize("hasPermission(null, 'CUSTOMER') or hasPermission(null, 'DRIVER')")
    public ApiResponse<?> confirmCompleteDelivery(@Valid @RequestBody UpdateStatusTripBookingsRequestDTO requestDTO, @RequestAttribute("role") String role, @PathVariable("bookingId") Integer bookingId) {
        tripBookingsService.confirmCompleteDelivery(requestDTO, role, bookingId);
        return new ApiResponse<>("Trip booking confirmed delivery successfully");
    }
}
