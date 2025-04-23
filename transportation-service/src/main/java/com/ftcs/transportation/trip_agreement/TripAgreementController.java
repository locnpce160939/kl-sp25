package com.ftcs.transportation.trip_agreement;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.transportation.TransportationURL;
import com.ftcs.transportation.trip_agreement.model.TripAgreement;
import com.ftcs.transportation.trip_agreement.service.TripAgreementService;
import com.ftcs.transportation.trip_matching.service.TripAcceptanceService;
import com.ftcs.transportation.trip_matching.service.TripMatchingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(TransportationURL.TRIP_AGREEMENT)
@RequiredArgsConstructor
public class TripAgreementController {

    private final TripAgreementService tripAgreementService;

    @GetMapping
    public ApiResponse<?> getMatchedTrips(@RequestAttribute("accountId") Integer accountId,
                                          @RequestAttribute("role") String role) {
        return switch (role) {
            case "DRIVER" -> ApiResponse.success(tripAgreementService.getAllTripAgreementsOfDriver(accountId));
            case "CUSTOMER" -> ApiResponse.success(tripAgreementService.getAllTripAgreementsOfCustomer(accountId));
            case "ADMIN" -> ApiResponse.success(tripAgreementService.getAllTripAgreements());
            default -> throw new BadRequestException("Error with role: " + role);
        };
    }

    @GetMapping("/{scheduleId}")
    public ApiResponse<Page<TripAgreement>> getTripAgreement(@PathVariable("scheduleId") Long scheduleId,
                                                             @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                             @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<TripAgreement> tripAgreement = tripAgreementService.getAllTripAgreementByScheduleId(scheduleId, page, size);
        return new ApiResponse<>(tripAgreement);
    }

}
