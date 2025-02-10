package com.ftcs.transportation.trip_booking;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.transportation.TransportationURL;
import com.ftcs.transportation.trip_booking.dto.*;
import com.ftcs.transportation.trip_booking.model.TripBookings;
import com.ftcs.transportation.trip_booking.service.TripBookingsService;
import com.ftcs.transportation.trip_matching.service.DirectionsService;
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
    private final DirectionsService directionsService;

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
                                                  @PathVariable("bookingId") Long bookingId) {
        tripBookingsService.updateTripBookings(requestDTO, bookingId);
        return new ApiResponse<>("Trip booking updated successfully");
    }

    @PutMapping("/cancel/{bookingId}")
    @PreAuthorize("hasPermission(null, 'CUSTOMER')")
    public ApiResponse<String> cancelTripBookings(@Valid @PathVariable("bookingId") Long bookingId) {
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
    public ApiResponse<TripBookingsDetailDTO> getTripBookings(@PathVariable("bookingId") Long bookingId,
                                                              @RequestAttribute("accountId") Integer accountId) {
        TripBookingsDetailDTO detailDTO = tripBookingsService.getTripBookingDetails(bookingId, accountId);
        return new ApiResponse<>(detailDTO);
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
                                                     @PathVariable("bookingId") Long bookingId) {
        tripBookingsService.updateStatusForDriver(requestDTO, accountId, bookingId);
        return new ApiResponse<>("Driver status updated successfully");
    }

    @PutMapping("/continueFindingDriver/{bookingId}")
    @PreAuthorize("hasPermission(null, 'CUSTOMER')")
    public ApiResponse<String> continueFindingDriver(@Valid @RequestBody UpdateStatusTripBookingsRequestDTO requestDTO,
                                                     @PathVariable("bookingId") Long bookingId) {
        tripBookingsService.continueFindingDriver(requestDTO, bookingId);
        return new ApiResponse<>("Continuing to find driver");
    }

    @PutMapping("/confirmCompleteDelivery/{bookingId}")
    @PreAuthorize("hasPermission(null, 'CUSTOMER') or hasPermission(null, 'DRIVER')")
    public ApiResponse<?> confirmCompleteDelivery(@Valid @RequestBody UpdateStatusTripBookingsRequestDTO requestDTO,
                                                  @RequestAttribute("role") String role,
                                                  @PathVariable("bookingId") Long bookingId) {
        tripBookingsService.confirmCompleteDelivery(requestDTO, role, bookingId);
        return new ApiResponse<>("Trip booking confirmed delivery successfully");
    }

    @GetMapping("/getByAccountId")
    @PreAuthorize("hasPermission(null, 'CUSTOMER')")
    public ApiResponse<List<TripBookings>> getByAccountId(@RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(tripBookingsService.getTripBookingsByAccountId(accountId));
    }

    @GetMapping("/getByAccountId/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<List<TripBookings>> getByAccountIdOfAdminRole(@PathVariable("accountId") Integer accountId) {
        return new ApiResponse<>(tripBookingsService.getTripBookingsByAccountIdOfAdminRole(accountId));
    }

    @GetMapping("/schedule/{scheduleId}")
    public ApiResponse<List<TripBookings>> getByScheduleId(@PathVariable("scheduleId") Long scheduleId) {
        return new ApiResponse<>(tripBookingsService.getBySchedule(scheduleId));
    }

    @GetMapping("/direction")
    public ApiResponse<?> findDirection(@RequestParam("origin") String origin, @RequestParam("destination") String destination) {
        return new ApiResponse<>(directionsService.getDirections(origin, destination));
    }
}
