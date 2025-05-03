package com.ftcs.transportation.trip_booking;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftcs.accountservice.driver.identification.dto.DriverIdentificationRequestDTO;
import com.ftcs.common.dto.ApiResponse;
import com.ftcs.insuranceservice.insurance_claim.dto.InsuranceClaimRequestDTO;
import com.ftcs.insuranceservice.insurance_claim.model.InsuranceClaim;
import com.ftcs.transportation.TransportationURL;
import com.ftcs.transportation.trip_booking.dto.*;
import com.ftcs.transportation.trip_booking.model.TripBookings;
import com.ftcs.transportation.trip_booking.service.TripBookingsService;
import com.ftcs.transportation.trip_matching.service.DirectionsService;
import com.ftcs.voucherservice.dto.VoucherValidationDTO;
import com.ftcs.voucherservice.model.Voucher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(TransportationURL.TRIP_BOOKINGS)
public class TripBookingsController {
    private final TripBookingsService tripBookingsService;
    private final ObjectMapper objectMapper;

    @PostMapping("/create")
    @PreAuthorize("hasPermission(null, 'CUSTOMER')")
    public ApiResponse<TripBookingsDTO> createTripBookings(@Valid @RequestBody TripBookingsRequestDTO requestDTO,
                                                        @RequestAttribute("accountId") Integer accountId) {
        TripBookingsDTO tripBookings = tripBookingsService.createTripBookings(requestDTO, accountId);
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
    public ApiResponse<Page<TripBookings>> getAllTripBookings(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                              @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<TripBookings> tripBookings = tripBookingsService.getAllTripBookings(page, size);
        return new ApiResponse<>(tripBookings);
    }

    @GetMapping("/{bookingId}")
    public ApiResponse<TripBookingsDetailDTO> getTripBookings(@PathVariable("bookingId") Long bookingId,
                                                              @RequestAttribute("accountId") Integer accountId) {
        TripBookingsDetailDTO detailDTO = tripBookingsService.getTripBookingDetails(bookingId, accountId);
        return new ApiResponse<>(detailDTO);
    }

//    @PostMapping("/filter")
//    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
//    public ApiResponse<List<TripBookings>> filterTripBookings(@Valid @RequestBody FindTripBookingByTimePeriodRequestDTO requestDTO) {
//        List<TripBookings> tripBookings = tripBookingsService.filterTripBookings(requestDTO);
//        return new ApiResponse<>(tripBookings);
//    }

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
    public ApiResponse<Page<TripBookingsDTO>> getByAccountId(@RequestAttribute("accountId") Integer accountId,
                                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                          @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(tripBookingsService.getTripBookingsByAccountId(accountId, page, size));
    }

    @GetMapping("/getByAccountId/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<Page<TripBookings>> getByAccountIdOfAdminRole(@PathVariable("accountId") Integer accountId,
                                                                     @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                     @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(tripBookingsService.getTripBookingsByAccountIdOfAdminRole(accountId, page, size));
    }

    @GetMapping("/schedule/{scheduleId}")
    public ApiResponse<List<TripBookings>> getByScheduleId(@PathVariable("scheduleId") Long scheduleId) {
        return new ApiResponse<>(tripBookingsService.getBySchedule(scheduleId));
    }

    @PostMapping("/direction")
    public ApiResponse<?> findDirection(@RequestBody TripBookingsRequestDTO requestDTO,
                                        @RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(tripBookingsService.getPreviewTripBookingDTO(accountId, requestDTO));
    }

//    @GetMapping("/insurance")
//    public ApiResponse<?> findDirection(@RequestParam("originalPrice") Double originalPrice,
//                                        @RequestParam("bookingType") Long bookingType){
//        return new ApiResponse<>(tripBookingsService.getPreviewInsuranceDTO(originalPrice, bookingType));
//    }

    @PutMapping("/updateStatus/{bookingId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> updateStatus(@Valid @RequestBody UpdateStatusTripBookingsRequestDTO requestDTO,
                                       @PathVariable("bookingId") Long bookingId){
        tripBookingsService.updateStatusTripBooking(requestDTO, bookingId);
        return new ApiResponse<>("Trip booking status updated successfully");
    }

    @PutMapping("/changePaymentMethod/{bookingId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> changePaymentMethod(@Valid @RequestBody UpdateStatusTripBookingsRequestDTO requestDTO,
                                       @PathVariable("bookingId") Long bookingId){
        tripBookingsService.changPaymentMethod(requestDTO, bookingId);
        return new ApiResponse<>("Change PaymentMethod successfully");
    }

    @PostMapping("/applicable")
    @PreAuthorize("hasPermission(null, 'CUSTOMER')")
    public ApiResponse<List<Voucher>> getApplicableVouchers(@Valid @RequestBody VoucherValidationDTO validationDTO) {
        return new ApiResponse<>(tripBookingsService.getApplicableVouchersForUser(validationDTO));
    }

    @PostMapping("/calculate-discount")
    @PreAuthorize("hasPermission(null, 'CUSTOMER')")
    public ApiResponse<VoucherDiscountDTO> calculateDiscount(
            @RequestParam(value = "voucherId", required = false) Long voucherId,
            @RequestParam(value = "voucherCode", required = false) String voucherCode,
            @Valid @RequestBody VoucherValidationDTO validationDTO) throws JsonProcessingException {
        return new ApiResponse<>(tripBookingsService.calculateVoucherDiscount(
               voucherId, voucherCode, validationDTO));
    }

    @PostMapping("/insuranceClaim/{bookingId}")
    @PreAuthorize("hasPermission(null, 'CUSTOMER')")
    public ApiResponse<?> createInsuranceClaim(
            @PathVariable("bookingId") Long bookingId,
            @RequestPart("data") String requestDTO,
            @RequestPart("images") List<MultipartFile> images)  throws JsonProcessingException{
        tripBookingsService.createInsuranceClaim(bookingId, objectMapper.readValue(requestDTO, InsuranceClaimRequestDTO.class), images);
        return new ApiResponse<>("Insurance claim created successfully");
    }
}
