package com.ftcs.transportation.trip_booking.service;

import com.ftcs.accountservice.account.serivce.AccountService;
import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.authservice.features.account.contacts.Rank;
import com.ftcs.balanceservice.balance_history.service.BalanceHistoryService;
import com.ftcs.balanceservice.payment.model.Payment;
import com.ftcs.balanceservice.payment.service.PaymentService;
import com.ftcs.bonusservice.constant.BonusTier;
import com.ftcs.bonusservice.constant.DriverGroup;
import com.ftcs.bonusservice.model.BonusConfiguration;
import com.ftcs.bonusservice.model.DriverBonusProgress;
import com.ftcs.bonusservice.repository.BonusConfigurationRepository;
import com.ftcs.bonusservice.repository.DriverBonusProgressRepository;
import com.ftcs.bonusservice.service.DriverBonusProgressService;
import com.ftcs.common.exception.BadRequestException;
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
import com.ftcs.voucherservice.constant.UserType;
import com.ftcs.voucherservice.constant.VoucherStatus;
import com.ftcs.voucherservice.constant.VoucherType;
import com.ftcs.voucherservice.dto.VoucherValidationDTO;
import com.ftcs.voucherservice.model.Voucher;
import com.ftcs.voucherservice.model.VoucherUsage;
import com.ftcs.voucherservice.repository.VoucherRepository;
import com.ftcs.voucherservice.repository.VoucherUsageRepository;
import com.ftcs.voucherservice.service.VoucherService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
    private final VoucherService voucherService;
    private final VoucherUsageRepository voucherUsageRepository;
    private final BonusConfigurationRepository bonusConfigurationRepository;
    private final DriverBonusProgressRepository driverBonusProgressRepository;
    private final DriverBonusProgressService driverBonusProgressService;
    private final AccountService accountService;
    private final VoucherRepository voucherRepository;



//    public TripBookingsDTO createTripBookings(TripBookingsRequestDTO requestDTO, Integer accountId) {
//        validateExpirationDate(requestDTO);
//
//        TripBookings tripBookings = new TripBookings();
//        tripBookings.setAccountId(accountId);
//        mapRequestToTripBookings(requestDTO, tripBookings);
//
//        PreviewTripBookingDTO preview = getPreviewTripBookingDTO(
//                requestDTO.getPickupLocation(),
//                requestDTO.getDropoffLocation(),
//                BigDecimal.valueOf(requestDTO.getCapacity())
//        );
//
//        tripBookings.setTotalDistance(preview.getExpectedDistance());
//        tripBookings.setPrice(preview.getPrice());
//
//        TripBookings savedBooking = tripBookingsRepository.save(tripBookings);
//
//        Payment payment = null;
//        if (savedBooking.getPaymentMethod() == PaymentMethod.ONLINE_PAYMENT) {
//            payment = paymentService.createPayment(savedBooking.getBookingId(), savedBooking.getPrice(), accountId);
//        }
//
//        tripMatchingService.matchTripsForAll();
//        return toTripBookingsDTO(savedBooking, payment);
//
//    }


    public TripBookingsDTO createTripBookings(TripBookingsRequestDTO requestDTO, Integer accountId) {
        validateExpirationDate(requestDTO);

        // Create and initialize trip booking
        TripBookings tripBookings = initializeNewTripBooking(accountId, requestDTO);

        // Get trip preview with distance and price calculation
        PreviewTripBookingDTO preview = getPreviewTripBookingDTO(
                accountId,
                requestDTO.getPickupLocation(),
                requestDTO.getDropoffLocation(),
                BigDecimal.valueOf(requestDTO.getCapacity())
        );

        tripBookings.setTotalDistance(preview.getExpectedDistance());
        tripBookings.setOriginalPrice(preview.getPrice());

        // Apply voucher if available
        applyVoucherIfAvailable(requestDTO, accountId, tripBookings, preview);

        // Save the booking
        TripBookings savedBooking = tripBookingsRepository.save(tripBookings);

        // Create payment if online payment method is selected
        Payment payment = createPaymentIfNeeded(savedBooking);

        // Start the trip matching process
        tripMatchingService.matchTripsForAll();

        return toTripBookingsDTO(savedBooking, payment);
    }

    private TripBookings initializeNewTripBooking(Integer accountId, TripBookingsRequestDTO requestDTO) {
        TripBookings tripBookings = new TripBookings();
        tripBookings.setAccountId(accountId);
        mapRequestToTripBookings(requestDTO, tripBookings);
        return tripBookings;
    }

    private void applyVoucherIfAvailable(TripBookingsRequestDTO requestDTO, Integer accountId,
                                         TripBookings tripBookings, PreviewTripBookingDTO preview) {
        // Create validation DTO for voucher checking
        VoucherValidationDTO validationDTO = VoucherValidationDTO.builder()
                .orderValue(preview.getPrice())
                .paymentMethod(requestDTO.getPaymentMethod().toString())
                .distanceKm(preview.getExpectedDistance())
                .isFirstOrder(isFirstOrder(accountId))
                .accountId(accountId)
                .build();

        // Calculate discount based on voucher
        VoucherDiscountDTO discountInfo = calculateVoucherDiscount(
                requestDTO.getVoucherId(),
                requestDTO.getVoucherCode(),
                validationDTO
        );

        // Apply voucher if discount exists
        if (discountInfo.getDiscountAmount() > 0) {
            applyVoucherDiscount(requestDTO, accountId, tripBookings, discountInfo);
        } else {
            // No voucher applied
            tripBookings.setPrice(preview.getPrice());
        }
    }

    private void applyVoucherDiscount(TripBookingsRequestDTO requestDTO, Integer accountId,
                                      TripBookings tripBookings, VoucherDiscountDTO discountInfo) {
        // Apply voucher by ID if provided
        if (requestDTO.getVoucherId() != null) {
            Voucher voucher = voucherService.findVoucherById(requestDTO.getVoucherId());
            tripBookings.setVoucherId(requestDTO.getVoucherId());
            tripBookings.setVoucherCode(voucher.getCode());
            voucherService.updateVoucherUsage(requestDTO.getVoucherId(), accountId);
        }
        // Apply voucher by code if provided
        else if (requestDTO.getVoucherCode() != null && !requestDTO.getVoucherCode().isEmpty()) {
            Voucher voucher = voucherService.findVoucherByCode(requestDTO.getVoucherCode());
            tripBookings.setVoucherId(voucher.getVoucherId());
            tripBookings.setVoucherCode(requestDTO.getVoucherCode());
            voucherService.applyVoucher(requestDTO.getVoucherCode(), accountId);
        }

        // Update price with discount
        tripBookings.setPrice(discountInfo.getFinalPrice());
        tripBookings.setDiscountAmount(discountInfo.getDiscountAmount());
    }

    private Payment createPaymentIfNeeded(TripBookings booking) {
        if (booking.getPaymentMethod() == PaymentMethod.ONLINE_PAYMENT) {
            return paymentService.createPayment(
                    booking.getBookingId(),
                    booking.getPrice(),
                    booking.getAccountId()
            );
        }
        return null;
    }

    public List<Voucher> getApplicableVouchersForUser(VoucherValidationDTO validationDTO) {
        List<Voucher> activeVouchers = voucherService.findAllByStatus(VoucherStatus.ACTIVE);
        List<Voucher> result = new ArrayList<>();

        if (validationDTO.getAccountId() != null) {
            List<VoucherUsage> userVoucherUsages = voucherUsageRepository.findByAccountId(validationDTO.getAccountId());

            for (VoucherUsage usage : userVoucherUsages) {
                try {
                    Voucher voucher = voucherService.findVoucherById(usage.getVoucherId());

                    System.out.println("Voucher ID: " + voucher.getVoucherId() +
                            ", Code: " + voucher.getCode() +
                            ", isRedeemed: " + usage.getIsRedeemed() +
                            ", usageCount: " + usage.getUsageCount() +
                            ", usageLimit: " + voucher.getUsageLimit());

                    if (voucher.getStatus() == VoucherStatus.ACTIVE) {
                        if (voucher.getVoucherType() == VoucherType.REDEMPTION) {
                            if (usage.getIsRedeemed()) {
                                if (voucher.getUsageLimit() == null || usage.getUsageCount() < voucher.getUsageLimit()) {
                                    result.add(voucher);
                                }
                            }
                        } else {
                            if (voucher.getUsageLimit() == null || usage.getUsageCount() < voucher.getUsageLimit()) {
                                result.add(voucher);
                            }
                        }
                    }
                } catch (Exception e) {
                    throw new BadRequestException(e.getMessage());
                }
            }
        }

        // Thêm các voucher hệ thống (không phải redemption) vào danh sách
        for (Voucher voucher : activeVouchers) {
            if (voucher.getVoucherType() != VoucherType.REDEMPTION) {
                // Kiểm tra xem voucher đã có trong danh sách kết quả chưa
                if (result.stream().noneMatch(v -> v.getVoucherId().equals(voucher.getVoucherId()))) {
                    result.add(voucher);
                }
            }
        }

        // Áp dụng các điều kiện khác
        return result.stream()
                .filter(voucher -> voucherService.isVoucherApplicable(voucher, validationDTO))
                .collect(Collectors.toList());
    }

    public VoucherDiscountDTO calculateVoucherDiscount(Long voucherId,
                                                       String voucherCode, VoucherValidationDTO validationDTO) {

        VoucherDiscountDTO result = new VoucherDiscountDTO();
        Double discountAmount = 0.0;
        Double finalPrice = validationDTO.getOrderValue();

        // Kiểm tra nếu có voucherId
        if (voucherId != null) {
            boolean isApplicable = voucherService.isVoucherApplicable(voucherId, validationDTO);

            if (isApplicable) {
                // Tính số tiền được giảm
                discountAmount = voucherService.calculateDiscountAmount(voucherId, validationDTO.getOrderValue());
                finalPrice = validationDTO.getOrderValue() - discountAmount;
            }
        }
        // Kiểm tra nếu có voucherCode
        else if (voucherCode != null && !voucherCode.isEmpty()) {
            boolean isApplicable = voucherService.isVoucherApplicableByCode(voucherCode, validationDTO);

            if (isApplicable) {
                // Tính số tiền được giảm
                discountAmount = voucherService.calculateDiscountAmount(voucherCode, validationDTO.getOrderValue());
                finalPrice = validationDTO.getOrderValue() - discountAmount;
            }
        }

        // Cập nhật kết quả
        result.setDiscountAmount(discountAmount);
        result.setFinalPrice(finalPrice);

        return result;
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


    public Page<TripBookings> getAllTripBookings(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return tripBookingsRepository.findAll(pageable);
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
        TripBookings tripBooking = findTripBookingsById(bookingId);

        // Check if booking is already completed
        if (isAlreadyCompleted(tripBooking, requestDTO)) {
            throw new BadRequestException("Trip booking has already been completed.");
        }

        // Handle trip completion if needed
        if (requestDTO.getStatus() == TripBookingStatus.ORDER_COMPLETED) {
            processOrderCompletion(tripBooking, bookingId);
        }

        // Update booking status
        tripBooking.setStatus(requestDTO.getStatus());
        tripBookingsRepository.save(tripBooking);
    }

    private boolean isAlreadyCompleted(TripBookings tripBooking, UpdateStatusTripBookingsRequestDTO requestDTO) {
        return tripBooking.getStatus() == TripBookingStatus.ORDER_COMPLETED &&
                requestDTO.getStatus() == TripBookingStatus.ORDER_COMPLETED;
    }

    private void processOrderCompletion(TripBookings tripBooking, Long bookingId) {
        log.info("Publishing trip completion event for booking: {}", bookingId);

        // Get required entities
        TripAgreement tripAgreement = getTripAgreement(tripBooking.getTripAgreementId());
        Schedule schedule = findScheduleByScheduleId(tripAgreement.getScheduleId());
        Account driverAccount = findAccountByAccountId(schedule);
        Account customerAccount = getCustomerAccount(tripBooking.getAccountId());

        // Process payment to driver
        processDriverPayment(driverAccount, tripBooking, bookingId);

        // Update loyalty points for both driver and customer
        updateLoyaltyPointsAndRanking(driverAccount, tripBooking.getPrice());
        updateLoyaltyPointsAndRanking(customerAccount, tripBooking.getPrice());

        // Save updated accounts
        accountRepository.save(driverAccount);
        accountRepository.save(customerAccount);

        // Update driver bonus progress
        updateDriverBonusProgress(driverAccount, tripBooking.getPrice());
    }

    private Account getCustomerAccount(Integer accountId) {
        return accountRepository.findAccountByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("Customer account not found"));
    }

    private void processDriverPayment(Account driverAccount, TripBookings tripBooking, Long bookingId) {
        // Update driver balance
        driverAccount.setBalance(driverAccount.getBalance() + tripBooking.getPrice());

        // Record payment in balance history
        balanceHistoryService.recordPaymentCredit(
                bookingId,
                driverAccount.getAccountId(),
                tripBooking.getPrice()
        );
    }

    private void updateDriverBonusProgress(Account driverAccount, Double tripPrice) {
        try {
            int currentMonth = LocalDateTime.now().getMonthValue();
            Optional<DriverBonusProgress> monthlyBonusOpt = driverBonusProgressRepository
                    .findByAccountIdAndBonusMonth(
                            driverAccount.getAccountId(),
                            currentMonth);

            if (monthlyBonusOpt.isPresent()) {
                updateExistingBonusProgress(monthlyBonusOpt.get(), tripPrice);
            } else {
                createNewBonusProgress(driverAccount, tripPrice);
            }
        } catch (Exception e) {
            // Log error but don't prevent trip completion
            log.error("Failed to update driver bonus progress: {}", e.getMessage(), e);
        }
    }

    private void updateExistingBonusProgress(DriverBonusProgress progress, Double tripPrice) {
        // Get the bonus configuration to calculate progress
        BonusConfiguration bonusConfig = bonusConfigurationRepository.findById(progress.getBonusConfigId())
                .orElseThrow(() -> new RuntimeException("Bonus Configuration not found"));

        // Update progress stats
        progress.setCompletedTrips(progress.getCompletedTrips() + 1);
        progress.setCurrentRevenue(progress.getCurrentRevenue() + tripPrice);

        // Calculate and update progress percentage
        double overallProgress = calculateProgressPercentage(progress, bonusConfig);
        progress.setProgressPercentage(overallProgress);

        // Check if bonus is achieved
        checkAndUpdateBonusAchievement(progress, bonusConfig);

        // Save updated progress
        progress.setUpdatedAt(LocalDateTime.now());
        driverBonusProgressRepository.save(progress);

        log.info("Updated existing monthly bonus progress for driver: {}, progress: {}%, achieved: {}",
                progress.getAccountId(), progress.getProgressPercentage(), progress.getIsAchieved());
    }

    private void createNewBonusProgress(Account driverAccount, Double tripPrice) {
        DriverGroup driverGroup = determineDriverGroup(driverAccount);
        BonusTier bonusTier = driverBonusProgressService.determineBonusTier(driverAccount);
        int currentMonth = LocalDateTime.now().getMonthValue();

        // Find active bonus configuration for the driver's group and tier
        Optional<BonusConfiguration> activeBonusOpt = bonusConfigurationRepository
                .findActiveConfigurationForDriverGroupAndTier(
                        driverGroup,
                        bonusTier,
                        LocalDateTime.now()
                );

        if (activeBonusOpt.isPresent()) {
            BonusConfiguration activeBonus = activeBonusOpt.get();

            // Create new progress record
            DriverBonusProgress progress = createNewBonusProgressRecord(
                    driverAccount.getAccountId(),
                    activeBonus.getBonusConfigurationId(),
                    currentMonth,
                    tripPrice
            );

            // Calculate and update progress percentage
            double overallProgress = calculateProgressPercentage(progress, activeBonus);
            progress.setProgressPercentage(overallProgress);

            // Check if bonus is achieved immediately
            checkAndUpdateBonusAchievement(progress, activeBonus);

            // Save new progress
            driverBonusProgressRepository.save(progress);

            log.info("Created new monthly bonus progress for driver: {}, progress: {}%, achieved: {}",
                    driverAccount.getAccountId(), progress.getProgressPercentage(), progress.getIsAchieved());
        }
    }

    private DriverBonusProgress createNewBonusProgressRecord(Integer accountId, Long bonusConfigId,
                                                             int bonusMonth, Double initialRevenue) {
        return DriverBonusProgress.builder()
                .accountId(accountId)
                .bonusConfigId(bonusConfigId)
                .bonusMonth(bonusMonth)
                .completedTrips(1)
                .currentRevenue(initialRevenue)
                .progressPercentage(0.0)
                .isAchieved(false)
                .isRewarded(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private double calculateProgressPercentage(DriverBonusProgress progress, BonusConfiguration bonusConfig) {
        double tripProgress = 0.0;
        double revenueProgress = 0.0;

        // Calculate trip progress if there's a trip target
        if (bonusConfig.getTargetTrips() != null && bonusConfig.getTargetTrips() > 0) {
            tripProgress = (double) progress.getCompletedTrips() / bonusConfig.getTargetTrips() * 100;
        }

        // Calculate revenue progress if there's a revenue target
        if (bonusConfig.getRevenueTarget() != null && bonusConfig.getRevenueTarget() > 0) {
            revenueProgress = progress.getCurrentRevenue() / bonusConfig.getRevenueTarget() * 100;
        }

        // Overall progress is the minimum of both requirements
        if (bonusConfig.getTargetTrips() != null && bonusConfig.getRevenueTarget() != null) {
            return Math.min(tripProgress, revenueProgress);
        } else if (bonusConfig.getTargetTrips() != null) {
            return tripProgress;
        } else if (bonusConfig.getRevenueTarget() != null) {
            return revenueProgress;
        } else {
            return 0.0;
        }
    }

    private void checkAndUpdateBonusAchievement(DriverBonusProgress progress, BonusConfiguration bonusConfig) {
        boolean tripRequirementMet = (bonusConfig.getTargetTrips() == null) ||
                (progress.getCompletedTrips() >= bonusConfig.getTargetTrips());
        boolean revenueRequirementMet = (bonusConfig.getRevenueTarget() == null) ||
                (progress.getCurrentRevenue() >= bonusConfig.getRevenueTarget());

        // Set achieved only if both requirements are met and not already achieved
        if (tripRequirementMet && revenueRequirementMet && !progress.getIsAchieved()) {
            progress.setIsAchieved(true);
            progress.setAchievedDate(LocalDateTime.now());
            // Optionally, add a notification here
            // notificationService.sendBonusAchievedNotification(progress.getAccountId(), bonusConfig);
        }
    }

    // Helper method for driver group determination
    private DriverGroup determineDriverGroup(Account account) {
        if (account.getRanking() == Rank.BRONZE && account.getLoyaltyPoints() < 500) {
            return DriverGroup.NEWBIE;
        } else {
            return DriverGroup.REGULAR;
        }
    }

    private void updateLoyaltyPointsAndRanking(Account account, Double price) {
        int pointsEarned = price.intValue() / 1000 * 10;

        account.setLoyaltyPoints(account.getLoyaltyPoints() + pointsEarned);
        account.setRedeemablePoints(account.getRedeemablePoints() + pointsEarned);

        if (account.getLoyaltyPoints() >= 50000) {
            account.setRanking(Rank.PLATINUM);
        } else if (account.getLoyaltyPoints() >= 20000) {
            account.setRanking(Rank.GOLD);
        } else if (account.getLoyaltyPoints() >= 5000) {
            account.setRanking(Rank.SILVER);
        } else {
            account.setRanking(Rank.BRONZE);
        }
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

    public Page<TripBookings> getTripBookingsByAccountId(Integer accountId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return tripBookingsRepository.findAllByAccountId(accountId, pageable);
    }

    public Page<TripBookings> getTripBookingsByAccountIdOfAdminRole(Integer accountId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return tripBookingsRepository.findAllByAccountId(accountId, pageable);
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

    public PreviewTripBookingDTO getPreviewTripBookingDTO(Integer accountId, String origin, String destination, BigDecimal weight) {
        DirectionsResponseDTO directionsDTO = directionsService.getDirections(origin, destination);
        double distance = directionsDTO.getRoutes().get(0).getLegs().get(0).getDistance().getValue() / 1000.0;
        BasePriceProjection basePriceProjection = tripBookingsRepository.findBasePrice(BigDecimal.valueOf(distance), weight);
        Boolean isFirstOrder = isFirstOrder(accountId);
        return getPreviewTripBookingDTO(weight, basePriceProjection, distance, isFirstOrder);
    }

    private static @NotNull PreviewTripBookingDTO getPreviewTripBookingDTO(BigDecimal weight, BasePriceProjection basePriceProjection, double distance, Boolean isFirstOrder) {
        if (basePriceProjection == null || basePriceProjection.getBasePrice() == null) {
            throw new RuntimeException("Base price not found for the given distance and weight");
        }

        BigDecimal basePrice = basePriceProjection.getBasePrice();
        double totalPrice = basePrice.multiply(weight).multiply(BigDecimal.valueOf(distance)).doubleValue();

        PreviewTripBookingDTO previewTripBookingDTO = new PreviewTripBookingDTO();
        previewTripBookingDTO.setPrice(totalPrice);
        previewTripBookingDTO.setExpectedDistance(distance);
        previewTripBookingDTO.setIsFirstOrder(isFirstOrder);
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
        tripBookings.setOriginalPrice(tripBookings.getOriginalPrice());
        tripBookings.setDiscountAmount(tripBookings.getDiscountAmount());
        tripBookings.setVoucherCode(tripBookings.getVoucherCode());
        tripBookings.setVoucherId(tripBookings.getVoucherId());
        tripBookings.setNotes(requestDTO.getNotes());
    }

    private Boolean isFirstOrder(Integer accountId) {
        return !tripBookingsRepository.existsByAccountId(accountId);
    }
}
