package com.ftcs.transportation.trip_booking.service;

import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.transportation.schedule.constant.ScheduleStatus;
import com.ftcs.transportation.schedule.model.Schedule;
import com.ftcs.transportation.schedule.repository.ScheduleRepository;
import com.ftcs.transportation.trip_agreement.model.TripAgreement;
import com.ftcs.transportation.trip_agreement.repository.TripAgreementRepository;
import com.ftcs.transportation.trip_booking.constant.TripBookingStatus;
import com.ftcs.transportation.trip_booking.dto.TripBookingsDetailDTO;
import com.ftcs.transportation.trip_booking.dto.TripBookingsRequestDTO;
import com.ftcs.transportation.trip_booking.dto.UpdateStatusTripBookingsRequestDTO;
import com.ftcs.transportation.trip_booking.model.TripBookings;
import com.ftcs.transportation.trip_booking.repository.TripBookingsRepository;
import com.ftcs.transportation.trip_matching.dto.DirectionsResponseDTO;
import com.ftcs.transportation.trip_matching.service.DirectionsService;
import com.ftcs.transportation.trip_matching.service.TripMatchingService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.ftcs.transportation.trip_booking.mapper.TripBookingsMapper.toDTO;

@Service
@AllArgsConstructor
public class TripBookingsService {
    private final TripMatchingService tripMatchingService;
    private static final Logger logger = LoggerFactory.getLogger(TripBookingsService.class);

    private final TripBookingsRepository tripBookingsRepository;
    private final ScheduleRepository scheduleRepository;
    private final TripAgreementRepository tripAgreementRepository;
    private final AccountRepository accountRepository;
    private final DirectionsService directionsService; // Add this dependency

    public TripBookings createTripBookings(TripBookingsRequestDTO requestDTO, Integer accountId) {
        validateExpirationDate(requestDTO);

        DirectionsResponseDTO directionsResponse = getDirectionsAndDistance(requestDTO);

        TripBookings tripBookings = new TripBookings();
        tripBookings.setAccountId(accountId);
        mapRequestToTripBookings(requestDTO, tripBookings);

        // Set total distance if available
        if (directionsResponse != null &&
                !directionsResponse.getRoutes().isEmpty() &&
                !directionsResponse.getRoutes().get(0).getLegs().isEmpty()) {

            DirectionsResponseDTO.LegDto firstLeg = directionsResponse.getRoutes().get(0).getLegs().get(0);
            if (firstLeg.getDistance() != null) {
                // Convert meters to kilometers
                double distanceInKm = firstLeg.getDistance().getValue() / 1000.0;
                tripBookings.setTotalDistance(distanceInKm);
            }
        }

        tripBookingsRepository.save(tripBookings);
        tripMatchingService.matchTripsForAll();
        return tripBookings;
    }

    private DirectionsResponseDTO getDirectionsAndDistance(TripBookingsRequestDTO requestDTO) {
        try {
            // Convert addresses to coordinates format "lat,lng"
            String origin = requestDTO.getPickupLocation();
            String destination = requestDTO.getDropoffLocation();

            return directionsService.getDirections(origin, destination);
        } catch (Exception e) {
            logger.error("Error getting directions: ", e);
            return null;
        }
    }

    public void updateTripBookings(TripBookingsRequestDTO requestDTO, Long bookingId) {
        validateExpirationDate(requestDTO);
        TripBookings tripBookings = findTripBookingsById(bookingId);
        mapRequestToTripBookings(requestDTO, tripBookings);
        tripBookingsRepository.save(tripBookings);
    }

    public void cancelTripBookings(Long bookingId) {
        TripBookings tripBookings = findTripBookingsById(bookingId);
        validateCancellationStatus(tripBookings);
        tripBookings.setStatus(TripBookingStatus.CANCELLED);
        tripBookingsRepository.save(tripBookings);
    }

    public TripBookingsDetailDTO getTripBookingDetails(Long bookingId, Integer accountId) {
        TripBookings tripBooking = findTripBookingsById(bookingId);

//        if (!tripBooking.getAccountId().equals(accountId)) {
//            throw new BadRequestException("No permission to access this booking");
//        }

        TripBookingsDetailDTO detailDTO = toDTO(tripBooking);
        TripAgreement tripAgreement = getTripAgreement(tripBooking.getTripAgreementId());

        detailDTO.setTripAgreement(tripAgreement);
        detailDTO.setDriver(getDriver(tripAgreement.getDriverId()));
        detailDTO.setCustomer(getDriver(tripAgreement.getCustomerId()));


        return detailDTO;
    }


    public List<TripBookings> getAllTripBookings() {
        return tripBookingsRepository.findAll();
    }

    public void updateStatusForDriver(UpdateStatusTripBookingsRequestDTO requestDTO, Integer accountId, Long bookingId) {
        TripBookings tripBookings = findTripBookingsById(bookingId);
        if (tripBookings.getStatus() == TripBookingStatus.ARRANGING_DRIVER) {
            handleDriverStatusUpdate(requestDTO, accountId, tripBookings);
        }else{
            throw new BadRequestException("Trip bookings status is not arranged");
        }
    }

    public void updateStatusTripBooking(UpdateStatusTripBookingsRequestDTO requestDTO, Long bookingId) {
        TripBookings tripBookings = findTripBookingsById(bookingId);
        tripBookings.setStatus(requestDTO.getStatus());
        tripBookingsRepository.save(tripBookings);
    }

    public void continueFindingDriver(UpdateStatusTripBookingsRequestDTO requestDTO, Long bookingId) {
        TripBookings tripBookings = findTripBookingsById(bookingId);
        if (tripBookings.getStatus() == TripBookingStatus.CANCELLED &&
                "Confirmed".equals(requestDTO.getOption())) {
            tripBookings.setStatus(TripBookingStatus.ARRANGING_DRIVER);
            tripBookingsRepository.save(tripBookings);
        }
    }

//    public List<TripBookings> filterTripBookings(FindTripBookingByTimePeriodRequestDTO requestDTO) {
//        boolean hasDateRange = requestDTO.getStartDate() != null && requestDTO.getEndDate() != null;
//        boolean hasStatus = requestDTO.getStatus() != null && !requestDTO.getStatus().isEmpty();
//        List<TripBookings> tripBookings;
//        if (hasDateRange && hasStatus) {
//            tripBookings = tripBookingsRepository.findAllByBookingDateBetweenAndStatus(
//                    requestDTO.getStartDate(),
//                    requestDTO.getEndDate(),
//                    requestDTO.getStatus()
//            );
//        } else if (hasDateRange) {
//            tripBookings = tripBookingsRepository.findAllByBookingDateBetween(
//                    requestDTO.getStartDate(),
//                    requestDTO.getEndDate()
//            );
//        } else if (hasStatus) {
//            tripBookings = tripBookingsRepository.findAllByStatus(requestDTO.getStatus());
//        } else {
//            tripBookings = tripBookingsRepository.findAll();
//        }
//        if (tripBookings.isEmpty()) {
//            throw new BadRequestException("No trip bookings found with the given criteria.");
//        }
//        return tripBookings;
//    }

    public void confirmCompleteDelivery(UpdateStatusTripBookingsRequestDTO requestDTO, String role, Long bookingId) {
        TripBookings tripBookings = findTripBookingsById(bookingId);
        if (isDriverConfirmingDelivery(role, tripBookings)) {
            updateBookingStatus(tripBookings, TripBookingStatus.DELIVERED);
        } else {
            isCustomerConfirmingCompletion(role, tripBookings, requestDTO);//completeOrder(tripBookings);
        }
    }

    public List<TripBookings> getTripBookingsByAccountId(Integer accountId) {
        return tripBookingsRepository.findAllByAccountId(accountId);
    }

    public List<TripBookings> getTripBookingsByAccountIdOfAdminRole(Integer accountId) {
        return tripBookingsRepository.findAllByAccountId(accountId);
    }

    public List<TripBookings> getBySchedule(Long scheduleId) {
        List<TripAgreement> tripAgreements = tripAgreementRepository.findAllByScheduleId(scheduleId);
        List<Long> tripBookingIds = tripAgreements.stream()
                .map(TripAgreement::getBookingId)
                .collect(Collectors.toList());

        return tripBookingsRepository.findAllById(tripBookingIds);
    }


    private boolean isDriverConfirmingDelivery(String role, TripBookings tripBookings) {
        return "DRIVER".equals(role) && (TripBookingStatus.DELIVERED == tripBookings.getStatus());
    }

    private boolean isCustomerConfirmingCompletion(String role, TripBookings tripBookings, UpdateStatusTripBookingsRequestDTO requestDTO) {
        return "CUSTOMER".equals(role)
                && (TripBookingStatus.DELIVERED == tripBookings.getStatus())
                && (requestDTO.getStatus() == TripBookingStatus.RECEIVED_THE_ITEM);
    }

    private void updateBookingStatus(TripBookings tripBookings, TripBookingStatus status) {
        tripBookings.setStatus(status);
        tripBookingsRepository.save(tripBookings);
    }


    private void validateExpirationDate(TripBookingsRequestDTO requestDTO) {
        if (!requestDTO.getExpirationDate().isAfter(requestDTO.getBookingDate())) {
            throw new BadRequestException("Expiration date must be after booking date");
        }
    }

    private TripBookings findTripBookingsById(Long bookingId) {
        return tripBookingsRepository.findTripBookingsByBookingId(bookingId)
                .orElseThrow(() -> new BadRequestException("Trip booking not found!"));
    }

    private Account getDriver(Integer accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new BadRequestException("Driver not found!"));
    }

    private TripBookings findTripBookingsByAccountId(Integer accountId) {
        return tripBookingsRepository.findTripBookingsByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("You haven't booked any trips yet!"));
    }

    private Schedule findScheduleByScheduleId(Long scheduleId) {
        return scheduleRepository.findScheduleByScheduleId(scheduleId)
                .orElseThrow(() -> new BadRequestException("Schedule not found!"));
    }

    private void validateCancellationStatus(TripBookings tripBookings) {
        if (TripBookingStatus.DRIVER_ON_THE_WAY == tripBookings.getStatus()) {
            throw new BadRequestException("You can't cancel because the driver is on the way");
        }
    }

    private TripAgreement getTripAgreement(Long tripAgreementId) {
        return tripAgreementRepository.findById(tripAgreementId)
                .orElseThrow(() -> new BadRequestException("Trip agreement not found!"));
    }
    private void handleDriverStatusUpdate(UpdateStatusTripBookingsRequestDTO requestDTO,
                                          Integer accountId, TripBookings tripBookings) {
        Schedule schedule = scheduleRepository.findScheduleByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("Schedule does not exist!"));
        if (ScheduleStatus.WAITING_FOR_DELIVERY != schedule.getStatus()) {
            throw new BadRequestException("Schedule status must be 'Waiting for delivery' to proceed.");
        }
        if ("Confirmed".equals(requestDTO.getOption())) {
            tripBookings.setStatus(TripBookingStatus.DRIVER_ON_THE_WAY);
            //tripBookings.setScheduleId(schedule.getScheduleId());
            schedule.setStatus(ScheduleStatus.GETTING_TO_THE_POINT);
            scheduleRepository.save(schedule);
        } else {
            tripBookings.setStatus(TripBookingStatus.CANCELLED);
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
        tripBookings.setStatus(TripBookingStatus.ARRANGING_DRIVER);
    }
}
