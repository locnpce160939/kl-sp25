package com.ftcs.transportation.trip_booking.service;

import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.balanceservice.balance_history.service.BalanceHistoryService;
import com.ftcs.balanceservice.payment.model.Payment;
import com.ftcs.balanceservice.payment.service.PaymentService;
import com.ftcs.common.exception.BadRequestException;
//import com.ftcs.payment.service.PaymentService;
import com.ftcs.transportation.schedule.constant.ScheduleStatus;
import com.ftcs.transportation.schedule.model.Schedule;
import com.ftcs.transportation.schedule.repository.ScheduleRepository;
import com.ftcs.transportation.trip_agreement.model.TripAgreement;
import com.ftcs.transportation.trip_agreement.repository.TripAgreementRepository;
import com.ftcs.transportation.trip_booking.constant.PaymentMethod;
import com.ftcs.transportation.trip_booking.constant.TripBookingStatus;
import com.ftcs.transportation.trip_booking.dto.*;
import com.ftcs.transportation.trip_booking.model.TripBookings;
import com.ftcs.transportation.trip_booking.projection.BasePriceProjection;
import com.ftcs.transportation.trip_booking.repository.TripBookingsRepository;
import com.ftcs.transportation.trip_matching.dto.DirectionsResponseDTO;
import com.ftcs.transportation.trip_matching.service.DirectionsService;
import com.ftcs.transportation.trip_matching.service.TripMatchingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.ftcs.transportation.trip_booking.mapper.TripBookingsMapper.toDTO;
import static com.ftcs.transportation.trip_booking.mapper.TripBookingsMapper.toTripBookingsDTO;

@Service
@AllArgsConstructor
@Slf4j
public class TripBookingsService {
    private final TripMatchingService tripMatchingService;
    private final BalanceHistoryService balanceHistoryService;
    private final TripBookingsRepository tripBookingsRepository;
    private final ScheduleRepository scheduleRepository;
    private final TripAgreementRepository tripAgreementRepository;
    private final AccountRepository accountRepository;
    private final DirectionsService directionsService;
    private final PaymentService paymentService;


    public TripBookingsDTO createTripBookings(TripBookingsRequestDTO requestDTO, Integer accountId) {
        validateExpirationDate(requestDTO);

        TripBookings tripBookings = new TripBookings();
        tripBookings.setAccountId(accountId);
        mapRequestToTripBookings(requestDTO, tripBookings);

        PreviewTripBookingDTO preview = getPreviewTripBookingDTO(
                requestDTO.getPickupLocation(),
                requestDTO.getDropoffLocation(),
                BigDecimal.valueOf(requestDTO.getCapacity())
        );

        tripBookings.setTotalDistance(preview.getExpectedDistance());
        tripBookings.setPrice(preview.getPrice());

        TripBookings savedBooking = tripBookingsRepository.save(tripBookings);

        Payment payment = null;
        if (savedBooking.getPaymentMethod() == PaymentMethod.ONLINE_PAYMENT) {
            payment = paymentService.createPayment(savedBooking.getBookingId(), savedBooking.getPrice(), accountId);
        }

        tripMatchingService.matchTripsForAll();
        return toTripBookingsDTO(savedBooking, payment);

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
        if (tripBookings.getStatus() == TripBookingStatus.ORDER_COMPLETED &&
                requestDTO.getStatus() == TripBookingStatus.ORDER_COMPLETED) {
            throw new BadRequestException("Trip booking has already been completed.");
        }
        if(requestDTO.getStatus() == TripBookingStatus.ORDER_COMPLETED){
            log.info("Publishing trip completion event for booking: {}", bookingId);
            TripAgreement tripAgreement = getTripAgreement(tripBookings.getTripAgreementId());
            Schedule schedule = findScheduleByScheduleId(tripAgreement.getScheduleId());
            Account account = findAccountByAccountId(schedule);
            account.setBalance(account.getBalance() + tripBookings.getPrice());
            accountRepository.save(account);
            balanceHistoryService.recordPaymentCredit(
                    bookingId,
                    account.getAccountId(),
                    tripBookings.getPrice()
            );
        }
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

    public void changPaymentMethod(UpdateStatusTripBookingsRequestDTO requestDTO, Long bookingId) {
        TripBookings tripBookings = findTripBookingsById(bookingId);
        tripBookings.setPaymentMethod(requestDTO.getPaymentMethod());
        tripBookingsRepository.save(tripBookings);
    }

    public void confirmCompleteDelivery(UpdateStatusTripBookingsRequestDTO requestDTO, String role, Long bookingId) {
        TripBookings tripBookings = findTripBookingsById(bookingId);
        if (isDriverConfirmingDelivery(role, tripBookings)) {
            updateBookingStatus(tripBookings, TripBookingStatus.DELIVERED);
        } else {
            isCustomerConfirmingCompletion(role, tripBookings, requestDTO);
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

    private Account findAccountByAccountId(Schedule schedule){
        return accountRepository.findAccountByAccountId(schedule.getAccountId())
                .orElseThrow(() -> new BadRequestException("Account not found"));
    }

    private Account getDriver(Integer accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new BadRequestException("Driver not found!"));
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

    public PreviewTripBookingDTO getPreviewTripBookingDTO(String origin, String destination, BigDecimal weight) {
        DirectionsResponseDTO directionsDTO = directionsService.getDirections(origin, destination);
        double distance = directionsDTO.getRoutes().get(0).getLegs().get(0).getDistance().getValue() / 1000.0;
        BasePriceProjection basePriceProjection = tripBookingsRepository.findBasePrice(BigDecimal.valueOf(distance), weight);
        return getPreviewTripBookingDTO(weight, basePriceProjection, distance);
    }

    private static @NotNull PreviewTripBookingDTO getPreviewTripBookingDTO(BigDecimal weight, BasePriceProjection basePriceProjection, double distance) {
        if (basePriceProjection == null || basePriceProjection.getBasePrice() == null) {
            throw new RuntimeException("Base price not found for the given distance and weight");
        }

        BigDecimal basePrice = basePriceProjection.getBasePrice();
        double totalPrice = basePrice.multiply(weight).multiply(BigDecimal.valueOf(distance)).doubleValue();

        PreviewTripBookingDTO previewTripBookingDTO = new PreviewTripBookingDTO();
        previewTripBookingDTO.setPrice(totalPrice);
        previewTripBookingDTO.setExpectedDistance(distance);
        return previewTripBookingDTO;
    }

    private void mapRequestToTripBookings(TripBookingsRequestDTO requestDTO, TripBookings tripBookings) {
        tripBookings.setPaymentMethod(requestDTO.getPaymentMethod());
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
