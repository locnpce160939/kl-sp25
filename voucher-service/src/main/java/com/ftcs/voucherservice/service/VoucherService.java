package com.ftcs.voucherservice.service;

import com.ftcs.accountservice.account.serivce.AccountService;
import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.authservice.features.account.contacts.Rank;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.voucherservice.constant.*;
import com.ftcs.voucherservice.dto.UpdateStatusVoucherRequestDTO;
import com.ftcs.voucherservice.dto.VoucherRequestDTO;
import com.ftcs.voucherservice.dto.VoucherValidationDTO;
import com.ftcs.voucherservice.model.Voucher;
import com.ftcs.voucherservice.model.VoucherUsage;
import com.ftcs.voucherservice.repository.VoucherRepository;
import com.ftcs.voucherservice.repository.VoucherUsageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class VoucherService {
    private final VoucherRepository voucherRepository;
    private final VoucherUsageRepository voucherUsageRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;

    public void createVoucher(VoucherRequestDTO requestDTO) {
        validateVoucher(requestDTO);
        VoucherType voucherType = (requestDTO.getPointsRequired() != null && requestDTO.getPointsRequired() > 0)
                ? VoucherType.REDEMPTION
                : VoucherType.SYSTEM;
        Voucher voucher = Voucher.builder()
                .code(requestDTO.getCode())
                .title(requestDTO.getTitle())
                .description(requestDTO.getDescription())
                .discountType(requestDTO.getDiscountType())
                .discountValue(requestDTO.getDiscountValue())
                .minOrderValue(requestDTO.getMinOrderValue())
                .maxDiscountAmount(requestDTO.getMaxDiscountAmount())
                .startDate(requestDTO.getStartDate())
                .endDate(requestDTO.getEndDate())
                .quantity(requestDTO.getQuantity())
                .isFirstOrder(requestDTO.getIsFirstOrder())
                .status(VoucherStatus.ACTIVE)
                .paymentMethod(requestDTO.getPaymentMethod())
                .minKm(requestDTO.getMinKm())
                .usageLimit(requestDTO.getUsageLimit())
                .userType(requestDTO.getUserType())
                .pointsRequired(requestDTO.getPointsRequired())
                .minimumRank(requestDTO.getMinimumRank())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .voucherType(voucherType)
                .build();
        voucherRepository.save(voucher);
    }

    public void updateVoucher(VoucherRequestDTO requestDTO, Long voucherId) {
        Voucher existingVoucher = findVoucherById(voucherId);
        validateVoucher(requestDTO);
        handleUpdateVoucher(existingVoucher, requestDTO);
    }

    public void updateVoucherStatus(Long voucherId, UpdateStatusVoucherRequestDTO requestDTO) {
        Voucher voucher = findVoucherById(voucherId);
        voucher.setStatus(requestDTO.getStatus());
        voucherRepository.save(voucher);
    }

    public Voucher findVoucherById(Long voucherId) {
        return voucherRepository.findById(voucherId)
                .orElseThrow(() -> new BadRequestException("Voucher not found!"));
    }

    public Voucher findVoucherByCode(String code) {
        return voucherRepository.findByCode(code)
                .orElseThrow(() -> new BadRequestException("Voucher with code " + code + " not found!"));
    }

    public List<Voucher> findAllByStatus(VoucherStatus status) {
        return voucherRepository.findAllByStatus(status);
    }

    public List<Voucher> findAllByStatusAndType(VoucherStatus status, VoucherType type) {
        return voucherRepository.findAllByStatusAndVoucherType(status, type);
    }

    public Page<Voucher> findAllByStatusManagement(VoucherStatus status, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return voucherRepository.findAllByStatus(status, pageable);
    }

    public Page<Voucher> findAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return voucherRepository.findAll(pageable);
    }

    public Page<Voucher> findAllActiveVouchers(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return voucherRepository.findByStatusNot(VoucherStatus.INACTIVE, pageable);
    }

    private void handleUpdateVoucher(Voucher existingVoucher, VoucherRequestDTO requestDTO) {
        existingVoucher.setTitle(requestDTO.getTitle());
        existingVoucher.setDescription(requestDTO.getDescription());
        existingVoucher.setDiscountType(requestDTO.getDiscountType());
        existingVoucher.setDiscountValue(requestDTO.getDiscountValue());
        existingVoucher.setMinOrderValue(requestDTO.getMinOrderValue());
        existingVoucher.setMaxDiscountAmount(requestDTO.getMaxDiscountAmount());
        existingVoucher.setStartDate(requestDTO.getStartDate());
        existingVoucher.setEndDate(requestDTO.getEndDate());
        existingVoucher.setQuantity(requestDTO.getQuantity());
        existingVoucher.setIsFirstOrder(requestDTO.getIsFirstOrder());
        existingVoucher.setPaymentMethod(requestDTO.getPaymentMethod());
        existingVoucher.setMinKm(requestDTO.getMinKm());
        existingVoucher.setUsageLimit(requestDTO.getUsageLimit());
        existingVoucher.setStatus(requestDTO.getStatus());
        existingVoucher.setUserType(requestDTO.getUserType());
        existingVoucher.setPointsRequired(requestDTO.getPointsRequired());
        existingVoucher.setMinimumRank(requestDTO.getMinimumRank());
        voucherRepository.save(existingVoucher);
    }

    // Core logic to check if account can use voucher
    private boolean canAccountUseVoucher(Integer accountId, Voucher voucher) {
        if (voucher.getUsageLimit() == null) {
            return true;
        }

        Optional<VoucherUsage> usageOpt =
                voucherUsageRepository.findByAccountIdAndVoucherId(accountId, voucher.getVoucherId());

        if (usageOpt.isEmpty()) {
            return true;
        }

        VoucherUsage usage = usageOpt.get();
        return usage.getUsageCount() < voucher.getUsageLimit();
    }

    public boolean isVoucherApplicable(Voucher voucher, VoucherValidationDTO validationDTO) {
        log.info("Checking voucher: {}", voucher.getVoucherId());
        log.info("Voucher details - Code: {}, Type: {}, Status: {}", 
            voucher.getCode(), voucher.getVoucherType(), voucher.getStatus());
        
        if (voucher.getStatus() != VoucherStatus.ACTIVE) {
            log.info("Voucher not active");
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getStartDate()) || now.isAfter(voucher.getEndDate())) {
            log.info("Voucher expired - Start: {}, End: {}, Now: {}", 
                voucher.getStartDate(), voucher.getEndDate(), now);
            return false;
        }

        if (voucher.getQuantity() != null && voucher.getQuantity() <= 0) {
            log.info("Voucher quantity depleted - Quantity: {}", voucher.getQuantity());
            return false;
        }

        // Check minimum order value
        if (voucher.getMinOrderValue() != null && validationDTO.getOrderValue() < voucher.getMinOrderValue()) {
            log.info("Order value too low - Order: {}, Min: {}", 
                validationDTO.getOrderValue(), voucher.getMinOrderValue());
            return false;
        }

        PaymentMethod mappedPaymentMethod = mapTripPaymentMethod(validationDTO.getPaymentMethod());
        if (voucher.getPaymentMethod() != PaymentMethod.ALL &&
                voucher.getPaymentMethod() != mappedPaymentMethod) {
            log.info("Payment method not matched - Voucher: {}, Order: {}", 
                voucher.getPaymentMethod(), mappedPaymentMethod);
            return false;
        }

        // Check minimum distance
        if (voucher.getMinKm() != null && validationDTO.getDistanceKm() < voucher.getMinKm()) {
            log.info("Distance too low - Order: {}, Min: {}", 
                validationDTO.getDistanceKm(), voucher.getMinKm());
            return false;
        }

        // Check first order condition
        if (voucher.getIsFirstOrder() != null && voucher.getIsFirstOrder() && !validationDTO.getIsFirstOrder()) {
            log.info("Not first order - Voucher requires first order");
            return false;
        }

        // Check for account specific validation
        if (validationDTO.getAccountId() != null) {
            log.info("Checking account validation for accountId: {}", validationDTO.getAccountId());
            Optional<VoucherUsage> usageOpt = voucherUsageRepository.findByAccountIdAndVoucherId(
                validationDTO.getAccountId(), 
                voucher.getVoucherId()
            );

            log.info("Voucher usage found: {}", usageOpt.isPresent());
            if (usageOpt.isPresent()) {
                VoucherUsage usage = usageOpt.get();
                log.info("Usage details - Count: {}, IsRedeemed: {}", 
                    usage.getUsageCount(), usage.getIsRedeemed());
            }

            // Check usage limit
            if (voucher.getUsageLimit() != null && usageOpt.isPresent()) {
                VoucherUsage usage = usageOpt.get();
                if (usage.getUsageCount() >= voucher.getUsageLimit()) {
                    log.info("Usage limit reached - Count: {}, Limit: {}", 
                        usage.getUsageCount(), voucher.getUsageLimit());
                    return false;
                }
            }

            // Check rank
            if (voucher.getMinimumRank() != null) {
                Account account = accountService.getAccountById(validationDTO.getAccountId());
                log.info("Rank check - User: {}, Required: {}", 
                    account.getRanking(), voucher.getMinimumRank());
                if (!isRankSufficient(account.getRanking(), voucher.getMinimumRank())) {
                    log.info("Rank not sufficient");
                    return false;
                }
            }

            // Check redemption
            if (voucher.getVoucherType() == VoucherType.REDEMPTION) {
                boolean hasRedeemed = usageOpt.map(VoucherUsage::getIsRedeemed).orElse(false);
                log.info("Redemption check - IsRedeemed: {}", hasRedeemed);
                if (!hasRedeemed) {
                    log.info("Voucher not redeemed");
                    return false;
                }
            }
        }

        log.info("Voucher is applicable");
        return true;
    }

    public boolean isVoucherApplicable(Long voucherId, VoucherValidationDTO validationDTO) {
        Voucher voucher = findVoucherById(voucherId);
        return isVoucherApplicable(voucher, validationDTO);
    }

    public boolean isVoucherApplicableByCode(String voucherCode, VoucherValidationDTO validationDTO) {
        try {
            Voucher voucher = findVoucherByCode(voucherCode);
            return isVoucherApplicable(voucher, validationDTO);
        } catch (BadRequestException e) {
            return false;
        }
    }

    private PaymentMethod mapTripPaymentMethod(String tripPaymentMethod) {
        if ("CASH".equalsIgnoreCase(tripPaymentMethod)) {
            return PaymentMethod.CASH_ONLY;
        } else if ("ONLINE_PAYMENT".equalsIgnoreCase(tripPaymentMethod)) {
            return PaymentMethod.ONLINE_ONLY;
        }
        throw new BadRequestException("Invalid payment method: " + tripPaymentMethod);
    }

    public Double calculateDiscountAmount(Long voucherId, Double orderValue) {
        Voucher voucher = findVoucherById(voucherId);
        return calculateDiscountAmount(voucher, orderValue);
    }

    public Double calculateDiscountAmount(String voucherCode, Double orderValue) {
        Voucher voucher = findVoucherByCode(voucherCode);
        return calculateDiscountAmount(voucher, orderValue);
    }

    private Double calculateDiscountAmount(Voucher voucher, Double orderValue) {
        Double discountAmount = 0.0;

        if (voucher.getDiscountType() == DiscountType.PERCENT) {
            discountAmount = orderValue * voucher.getDiscountValue() / 100;

            // Check and apply maximum discount limit
            if (voucher.getMaxDiscountAmount() != null && discountAmount > voucher.getMaxDiscountAmount()) {
                discountAmount = voucher.getMaxDiscountAmount();
            }
        } else if (voucher.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            discountAmount = voucher.getDiscountValue();

            // Ensure discount doesn't exceed order value
            if (discountAmount > orderValue) {
                discountAmount = orderValue;
            }
        }

        return discountAmount;
    }

    // Apply voucher and track account usage
    public void applyVoucher(String voucherCode, Integer accountId) {
        Voucher voucher = findVoucherByCode(voucherCode);

        // Update voucher quantities and status if needed
        if (voucher.getQuantity() != null) {
            voucher.setQuantity(voucher.getQuantity() - 1);

            if (voucher.getQuantity() <= 0) {
                voucher.setStatus(VoucherStatus.DEPLETED);
            }

            voucher.setUpdatedAt(LocalDateTime.now());
            voucherRepository.save(voucher);
        }

        // Track the usage by the account
        trackVoucherUsageByAccount(accountId, voucher.getVoucherId());
    }

    public void updateVoucherUsage(Long voucherId, Integer accountId) {
        Voucher voucher = findVoucherById(voucherId);
        if (voucher.getQuantity() != null) {
            voucher.setQuantity(voucher.getQuantity() - 1);

            if (voucher.getQuantity() <= 0) {
                voucher.setStatus(VoucherStatus.DEPLETED);
            }

            voucher.setUpdatedAt(LocalDateTime.now());
            voucherRepository.save(voucher);
        }
        trackVoucherUsageByAccount(accountId, voucherId);
    }

    // Track voucher usage by account
    public void trackVoucherUsageByAccount(Integer accountId, Long voucherId) {
        LocalDateTime now = LocalDateTime.now();

        VoucherUsage usage = voucherUsageRepository
                .findByAccountIdAndVoucherId(accountId, voucherId)
                .orElse(VoucherUsage.builder()
                        .accountId(accountId)
                        .voucherId(voucherId)
                        .usageCount(0)
                        .isRedeemed(false)
                        .createAt(now)
                        .build());

        // Increment usage count
        usage.setUsageCount(usage.getUsageCount() + 1);
        usage.setLastUsageAt(now);
        usage.setUpdateAt(now);

        // Do not modify redemption status - keep existing isRedeemed and redemptionDate values

        // Save usage record
        voucherUsageRepository.save(usage);
    }

    public void trackVoucherUsageByAccountWithCode(Integer accountId, String voucherCode) {
        Voucher voucher = findVoucherByCode(voucherCode);
        trackVoucherUsageByAccount(accountId, voucher.getVoucherId());
    }

    // Get account usage count for a voucher
    public int getAccountVoucherUsageCount(Integer accountId, Long voucherId) {
        return voucherUsageRepository
                .findByAccountIdAndVoucherId(accountId, voucherId)
                .map(VoucherUsage::getUsageCount)
                .orElse(0);
    }

    // Method to update voucher usage (original logic)
    private void updateVoucherUsage(Voucher voucher) {
        // Decrease remaining voucher quantity if limited
        if (voucher.getQuantity() != null) {
            voucher.setQuantity(voucher.getQuantity() - 1);

            // Check if depleted, update status
            if (voucher.getQuantity() <= 0) {
                voucher.setStatus(VoucherStatus.DEPLETED);
            }
        }

        voucher.setUpdatedAt(LocalDateTime.now());
        voucherRepository.save(voucher);
    }

    // Original methods without account ID - kept for backward compatibility
    public void applyVoucher(String voucherCode) {
        Voucher voucher = findVoucherByCode(voucherCode);
        updateVoucherUsage(voucher);
    }

    public void updateVoucherUsage(Long voucherId) {
        Voucher voucher = findVoucherById(voucherId);
        updateVoucherUsage(voucher);
    }

    public void autoUpdateVoucherStatus() {
        LocalDateTime now = LocalDateTime.now();
        List<Voucher> activeVouchers = voucherRepository.findAllByStatus(VoucherStatus.ACTIVE);

        for (Voucher voucher : activeVouchers) {
            // Check if voucher has expired
            if (now.isAfter(voucher.getEndDate())) {
                voucher.setStatus(VoucherStatus.EXPIRED);
                voucher.setUpdatedAt(now);
                voucherRepository.save(voucher);
            }

            // Check if voucher quantity is depleted
            else if (voucher.getQuantity() != null && voucher.getQuantity() <= 0) {
                voucher.setStatus(VoucherStatus.DEPLETED);
                voucher.setUpdatedAt(now);
                voucherRepository.save(voucher);
            }

            // Check if voucher usage limit is depleted
            else if (voucher.getUsageLimit() != null && voucher.getUsageLimit() <= 0) {
                voucher.setStatus(VoucherStatus.DEPLETED);
                voucher.setUpdatedAt(now);
                voucherRepository.save(voucher);
            }
        }
    }

    // Scheduled task to update voucher status
    @Scheduled(cron = "0 */5 * * * *")
    public void scheduledStatusUpdate() {
        log.info("Running scheduled voucher status update");
        autoUpdateVoucherStatus();
    }

    // Get applicable vouchers for user (with account limits considered)
    public List<Voucher> getApplicableVouchersForUser(Integer accountId, VoucherValidationDTO validationDTO) {
        List<Voucher> activeVouchers = findAllByStatus(VoucherStatus.ACTIVE);

        // Update validation DTO with account ID if not already set
        if (validationDTO.getAccountId() == null) {
            validationDTO.setAccountId(accountId);
        }

        // Filter vouchers applicable to conditions
        return activeVouchers.stream()
                .filter(voucher -> isVoucherApplicable(voucher, validationDTO))
                .collect(Collectors.toList());
    }

    public void deleteVoucher(Long voucherId) {
        Voucher voucher = findVoucherById(voucherId);
        voucher.setStatus(VoucherStatus.INACTIVE);
        voucherRepository.save(voucher);
    }

    public Voucher redeemVoucherWithPoints(Integer accountId, Long voucherId) {
        // Validate voucher and account
        Voucher voucher = validateVoucherForRedemption(voucherId);
        Account account = validateAccountForRedemption(accountId, voucher);
        
        // Check redemption limits
        validateRedemptionLimits(accountId, voucherId, voucher);
        
        // Process redemption
        return processVoucherRedemption(accountId, voucherId, voucher, account);
    }

    private Voucher validateVoucherForRedemption(Long voucherId) {
        Voucher voucher = findVoucherById(voucherId);
        
        if (voucher.getVoucherType() != VoucherType.REDEMPTION) {
            throw new BadRequestException("This voucher cannot be redeemed with points");
        }

        if (voucher.getStatus() != VoucherStatus.ACTIVE) {
            throw new BadRequestException("Voucher is not active");
        }

        if (voucher.getQuantity() != null && voucher.getQuantity() <= 0) {
            throw new BadRequestException("Voucher is out of stock");
        }

        return voucher;
    }

    private Account validateAccountForRedemption(Integer accountId, Voucher voucher) {
        Account account = accountService.getAccountById(accountId);

        if (voucher.getMinimumRank() != null && 
            !isRankSufficient(account.getRanking(), voucher.getMinimumRank())) {
            throw new BadRequestException("Your rank is insufficient for this voucher. Required: " + 
                    voucher.getMinimumRank());
        }

        if (account.getRedeemablePoints() < voucher.getPointsRequired()) {
            throw new BadRequestException("Insufficient points to redeem this voucher");
        }

        return account;
    }

    private void validateRedemptionLimits(Integer accountId, Long voucherId, Voucher voucher) {
        Optional<VoucherUsage> usageOpt = voucherUsageRepository.findByAccountIdAndVoucherId(accountId, voucherId);

        if (usageOpt.isPresent()) {
            VoucherUsage usage = usageOpt.get();
            
            if (voucher.getUsageLimit() != null && usage.getUsageCount() >= voucher.getUsageLimit()) {
                throw new BadRequestException("You have reached the maximum redemption limit for this voucher");
            }

            if (usage.getIsRedeemed()) {
                throw new BadRequestException("You have already redeemed this voucher");
            }
        }
    }

    private Voucher processVoucherRedemption(Integer accountId, Long voucherId, Voucher voucher, Account account) {
        LocalDateTime now = LocalDateTime.now();

        // Update account points
        updateAccountPoints(account, voucher.getPointsRequired());

        // Update voucher usage
        updateVoucherUsageRecord(accountId, voucherId, now);

        // Update voucher quantity if needed
        updateVoucherQuantity(voucher, now);

        return voucher;
    }

    private void updateAccountPoints(Account account, Integer pointsRequired) {
        account.setRedeemablePoints(account.getRedeemablePoints() - pointsRequired);
        accountRepository.save(account);
    }

    private void updateVoucherUsageRecord(Integer accountId, Long voucherId, LocalDateTime now) {
        VoucherUsage usage = voucherUsageRepository
                .findByAccountIdAndVoucherId(accountId, voucherId)
                .orElse(VoucherUsage.builder()
                        .accountId(accountId)
                        .voucherId(voucherId)
                        .usageCount(0)
                        .isRedeemed(false)
                        .createAt(now)
                        .build());

        usage.setIsRedeemed(true);
        usage.setRedemptionDate(now);
        usage.setUpdateAt(now);
        voucherUsageRepository.save(usage);
    }

    private void updateVoucherQuantity(Voucher voucher, LocalDateTime now) {
        if (voucher.getQuantity() != null) {
            voucher.setQuantity(voucher.getQuantity() - 1);

            if (voucher.getQuantity() <= 0) {
                voucher.setStatus(VoucherStatus.DEPLETED);
            }

            voucher.setUpdatedAt(now);
            voucherRepository.save(voucher);
        }
    }

    public List<Voucher> getVouchersAvailableForRedemption(Integer accountId, UserType userType) {
        // Get account details
        Account account = accountService.getAccountById(accountId);
        int availablePoints = account.getRedeemablePoints();
        Rank userRank = account.getRanking();

        // Find all active vouchers for the specified user type
        List<Voucher> activeVouchers = voucherRepository.findAllByStatusAndUserType(
                VoucherStatus.ACTIVE, userType);

        // Filter by points requirement, rank, and availability
        return activeVouchers.stream()
                .filter(v -> v.getPointsRequired() != null && v.getPointsRequired() <= availablePoints)
                .filter(v -> v.getQuantity() == null || v.getQuantity() > 0)
                .filter(v -> v.getMinimumRank() == null || isRankSufficient(userRank, v.getMinimumRank()))
                .collect(Collectors.toList());
    }

    public Page<Voucher> getAllVouchers(Boolean isRedeemable, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);

        if (isRedeemable) {
            // Get redeemable vouchers (points required > 0)
            return voucherRepository.findByPointsRequiredGreaterThan(0, pageable);
        } else {
            // Get system vouchers (points required = 0 or null)
            return voucherRepository.findSystemVouchers(pageable);
        }
    }

    private boolean isRankSufficient(Rank userRank, Rank requiredRank) {
        // Assuming ranks are ordered: BRONZE < SILVER < GOLD < etc.
        return userRank.ordinal() >= requiredRank.ordinal();
    }

    private void validateVoucher(VoucherRequestDTO requestDTO) {
        if (requestDTO.getDiscountType() == DiscountType.PERCENT) {
            if (requestDTO.getDiscountValue() == null || requestDTO.getDiscountValue() < 1 || requestDTO.getDiscountValue() > 100) {
                throw new BadRequestException("Discount value for percentage type must be between 1 and 100");
            }
        } else if (requestDTO.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            if (requestDTO.getDiscountValue() == null || requestDTO.getDiscountValue() <= 0) {
                throw new BadRequestException("Discount value for fixed amount type must be greater than 0");
            }
        }
        if (requestDTO.getStartDate() == null || requestDTO.getEndDate() == null) {
            throw new BadRequestException("Start date and end date cannot be null");
        }

        if (requestDTO.getStartDate().isAfter(requestDTO.getEndDate())) {
            throw new BadRequestException("Start date cannot be after end date");
        }

        if (voucherRepository.existsByCode(requestDTO.getCode())) {
            throw new BadRequestException("Voucher code already exists");
        }
    }
}