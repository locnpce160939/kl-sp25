package com.ftcs.insuranceservice.booking_type;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.insuranceservice.InsuranceURL;
import com.ftcs.insuranceservice.booking_type.dto.BookingTypeRequestDTO;
import com.ftcs.insuranceservice.booking_type.model.BookingType;
import com.ftcs.insuranceservice.booking_type.service.BookingTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(InsuranceURL.BOOKING_TYPE)
public class BookingTypeController {
    private final BookingTypeService bookingTypeService;

    @GetMapping
    public ApiResponse<Page<BookingType>> geAllBookingTypes(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(bookingTypeService.getAllBookingTypes(page, size));
    }

    @GetMapping("/{bookingTypeId}")
    public ApiResponse<BookingType> getById(@PathVariable("bookingTypeId") Long bookingTypeId) {
        return new ApiResponse<>(bookingTypeService.getBookingType(bookingTypeId));
    }

    @PostMapping
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'FINANCE')")
    public ApiResponse<BookingType> createBookingType(@Valid @RequestBody BookingTypeRequestDTO requestDTO) {
        return new ApiResponse<>(bookingTypeService.createBookingType(requestDTO));
    }

    @PutMapping("/{bookingTypeId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'FINANCE')")
    public ApiResponse<BookingType> updateBookingType(@Valid @RequestBody BookingTypeRequestDTO requestDTO,
                                                      @PathVariable("bookingTypeId") Long bookingTypeId) {
        return new ApiResponse<>(bookingTypeService.updateBookingType(requestDTO,bookingTypeId));
    }

    @DeleteMapping("/{bookingTypeId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'FINANCE')")
    public ApiResponse<?> deleteBookingType(@PathVariable("bookingTypeId") Long bookingTypeId) {
        bookingTypeService.deleteBookingType(bookingTypeId);
        return new ApiResponse<>("Deleted Booking Type");
    }
}
