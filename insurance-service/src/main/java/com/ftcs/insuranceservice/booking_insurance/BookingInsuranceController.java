package com.ftcs.insuranceservice.booking_insurance;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.insuranceservice.InsuranceURL;
import com.ftcs.insuranceservice.booking_insurance.model.BookingInsurance;
import com.ftcs.insuranceservice.booking_insurance.service.BookingInsuranceService;
import com.ftcs.insuranceservice.insurance_policy.model.InsurancePolicy;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(InsuranceURL.BOOKING_INSURANCE)
public class BookingInsuranceController {
    private final BookingInsuranceService bookingInsuranceService;

    @GetMapping
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'FINANCE')")
    public ApiResponse<Page<BookingInsurance>> getAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                      @RequestParam(value = "size", defaultValue = "10") Integer size){
        return new ApiResponse<>(bookingInsuranceService.getAllBookingInsurances(page, size));
    }

    @GetMapping("/{id}")
    public ApiResponse<BookingInsurance> getInsurancePolicy(@PathVariable("id") Long id){
        return new ApiResponse<>(bookingInsuranceService.getBookingInsuranceById(id));
    }

    @GetMapping("/account/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'FINANCE')")
    public ApiResponse<Page<BookingInsurance>> getAllByAccountManagement(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                      @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                      @PathVariable("accountId") Integer accountId){
        return new ApiResponse<>(bookingInsuranceService.getBookingInsurancesByAccountId(accountId, page, size));
    }

    @GetMapping("/account")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'FINANCE')")
    public ApiResponse<Page<BookingInsurance>> getAllByAccount(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                      @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                      @RequestAttribute("accountId") Integer accountId){
        return new ApiResponse<>(bookingInsuranceService.getBookingInsurancesByAccountId(accountId, page, size));
    }
}
