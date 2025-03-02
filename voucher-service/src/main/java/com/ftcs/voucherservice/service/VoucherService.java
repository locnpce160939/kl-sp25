package com.ftcs.voucherservice.service;

import com.ftcs.common.exception.BadRequestException;
import com.ftcs.voucherservice.constant.DiscountType;
import com.ftcs.voucherservice.constant.PaymentMethod;
import com.ftcs.voucherservice.constant.VoucherStatus;
import com.ftcs.voucherservice.dto.UpdateStatusVoucherRequestDTO;
import com.ftcs.voucherservice.dto.VoucherRequestDTO;
import com.ftcs.voucherservice.dto.VoucherValidationDTO;
import com.ftcs.voucherservice.model.Voucher;
import com.ftcs.voucherservice.model.VoucherUsage;
import com.ftcs.voucherservice.repository.VoucherRepository;
import com.ftcs.voucherservice.repository.VoucherUsageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public void createVoucher(VoucherRequestDTO requestDTO) {
        validateVoucher(requestDTO);
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
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
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

    public List<Voucher> findAll() {
        return voucherRepository.findAll();
    }

    public List<Voucher> findAllActiveVouchers() {
        return voucherRepository.findAll()
                .stream()
                .filter(v -> v.getStatus() != VoucherStatus.INACTIVE) // Nếu status là Enum
                .collect(Collectors.toList());
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
        voucherRepository.save(existingVoucher);
    }

    // Core logic to check if account can use voucher
    private boolean canAccountUseVoucher(Integer accountId, Voucher voucher) {
        // If voucher has no per-account limit, then it can be used
        if (voucher.getUsageLimit() == null) {
            return true;
        }

        // Check if account has used this voucher before
        Optional<VoucherUsage> usageOpt =
                voucherUsageRepository.findByAccountIdAndVoucherId(accountId, voucher.getVoucherId());

        if (usageOpt.isEmpty()) {
            // Account has never used this voucher
            return true;
        }

        // Check if account has reached the usage limit
        VoucherUsage usage = usageOpt.get();
        return usage.getUsageCount() < voucher.getUsageLimit();
    }

    // Modified voucher applicability check to include account limits
    public boolean isVoucherApplicable(Voucher voucher, VoucherValidationDTO validationDTO) {
        if (voucher.getStatus() != VoucherStatus.ACTIVE) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(voucher.getStartDate()) || now.isAfter(voucher.getEndDate())) {
            return false;
        }

        if (voucher.getQuantity() != null && voucher.getQuantity() <= 0) {
            return false;
        }

        // Check minimum order value
        if (voucher.getMinOrderValue() != null && validationDTO.getOrderValue() < voucher.getMinOrderValue()) {
            return false;
        }

        PaymentMethod mappedPaymentMethod = mapTripPaymentMethod(validationDTO.getPaymentMethod());

        if (voucher.getPaymentMethod() != PaymentMethod.ALL &&
                voucher.getPaymentMethod() != mappedPaymentMethod) {
            return false;
        }

        // Check minimum distance
        if (voucher.getMinKm() != null && validationDTO.getDistanceKm() < voucher.getMinKm()) {
            return false;
        }

        // Check first order condition
        if (voucher.getIsFirstOrder() != null && voucher.getIsFirstOrder() && !validationDTO.getIsFirstOrder()) {
            return false;
        }

        // Check account-specific usage limit
        if (validationDTO.getAccountId() != null &&
                !canAccountUseVoucher(validationDTO.getAccountId(), voucher)) {
            return false;
        }

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
        updateVoucherUsage(voucher);
        trackVoucherUsageByAccount(accountId, voucher.getVoucherId());
    }

    public void updateVoucherUsage(Long voucherId, Integer accountId) {
        Voucher voucher = findVoucherById(voucherId);
        updateVoucherUsage(voucher);
        trackVoucherUsageByAccount(accountId, voucherId);
    }

    // Track voucher usage by account
    public void trackVoucherUsageByAccount(Integer accountId, Long voucherId) {
        LocalDateTime now = LocalDateTime.now();

        // Find existing usage record or create new one
        VoucherUsage usage = voucherUsageRepository
                .findByAccountIdAndVoucherId(accountId, voucherId)
                .orElse(VoucherUsage.builder()
                        .accountId(accountId)
                        .voucherId(voucherId)
                        .usageCount(0)
                        .createAt(now)
                        .build());

        // Increment usage count
        usage.setUsageCount(usage.getUsageCount() + 1);
        usage.setLastUsageAt(now);
        usage.setUpdateAt(now);

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

        // Update usage limit if applicable
        if (voucher.getUsageLimit() != null) {
            // If usage limit reached, update status
            if (voucher.getUsageLimit() <= 0) {
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
                log.info("Voucher {} automatically updated to EXPIRED status", voucher.getCode());
            }

            // Check if voucher quantity is depleted
            else if (voucher.getQuantity() != null && voucher.getQuantity() <= 0) {
                voucher.setStatus(VoucherStatus.DEPLETED);
                voucher.setUpdatedAt(now);
                voucherRepository.save(voucher);
                log.info("Voucher {} automatically updated to DEPLETED status", voucher.getCode());
            }

            // Check if voucher usage limit is depleted
            else if (voucher.getUsageLimit() != null && voucher.getUsageLimit() <= 0) {
                voucher.setStatus(VoucherStatus.DEPLETED);
                voucher.setUpdatedAt(now);
                voucherRepository.save(voucher);
                log.info("Voucher {} automatically updated to DEPLETED status", voucher.getCode());
            }
        }
    }

    // Scheduled task to update voucher status
    @Scheduled(cron = "0 */5 * * * *") // Run every 5 minutes
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