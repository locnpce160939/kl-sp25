package com.ftcs.bonusservice.service;

import com.ftcs.accountservice.account.serivce.AccountService;
import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.authservice.features.account.contacts.Rank;
import com.ftcs.balanceservice.balance_history.service.BalanceHistoryService;
import com.ftcs.bonusservice.constant.BonusTier;
import com.ftcs.bonusservice.constant.DriverGroup;
import com.ftcs.bonusservice.dto.AccountDTO;
import com.ftcs.bonusservice.dto.BonusConfigurationDTO;
import com.ftcs.bonusservice.dto.DriverBonusProgressDTO;
import com.ftcs.bonusservice.model.BonusConfiguration;
import com.ftcs.bonusservice.model.DriverBonusProgress;
import com.ftcs.bonusservice.repository.BonusConfigurationRepository;
import com.ftcs.bonusservice.repository.DriverBonusProgressRepository;
import com.ftcs.common.exception.BadRequestException;
import io.swagger.models.auth.In;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DriverBonusProgressService {
    private final DriverBonusProgressRepository driverBonusProgressRepository;
    private final BonusConfigurationRepository bonusConfigurationRepository;
    private final BonusConfigurationService bonusConfigurationService;
    private final AccountService accountServiceClient;
    private final AccountRepository accountRepository;
    private final BalanceHistoryService balanceHistoryService;


    public Page<DriverBonusProgressDTO> getDriverProgressByAccountId(Integer accountId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DriverBonusProgress> configPage = driverBonusProgressRepository.findByAccountId(accountId, pageable);
        List<DriverBonusProgressDTO> dtoList = configPage.getContent()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, configPage.getTotalElements());
    }

    public Page<DriverBonusProgressDTO> getProgressByBonusConfigId(Long bonusConfigId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DriverBonusProgress> configPage =  driverBonusProgressRepository.findByBonusConfigId(bonusConfigId, pageable);
        List<DriverBonusProgressDTO> dtoList = configPage.getContent()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, configPage.getTotalElements());
    }

    public DriverBonusProgressDTO updateDriverProgress(Integer accountId, Long bonusConfigId,
                                                       Integer newTrips, Double newRevenue) {
        // Find or create progress record
        Optional<DriverBonusProgress> existingProgress =
                driverBonusProgressRepository.findByAccountIdAndBonusConfigId(accountId, bonusConfigId);

        BonusConfiguration bonusConfig = bonusConfigurationRepository.findById(bonusConfigId)
                .orElseThrow(() -> new RuntimeException("Bonus Configuration not found"));


        DriverBonusProgress progress;
        if (existingProgress.isPresent()) {
            progress = existingProgress.get();
            progress.setCompletedTrips(newTrips);
            progress.setCurrentRevenue(newRevenue);
        } else {
            progress = DriverBonusProgress.builder()
                    .accountId(accountId)
                    .bonusConfigId(bonusConfigId)
                    .completedTrips(newTrips)
                    .currentRevenue(newRevenue)
                    .progressPercentage(0.0)
                    .isAchieved(false)
                    .isRewarded(false)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
        }

        // Calculate progress percentage
        double progressPercentage = calculateProgressPercentage(progress, bonusConfig);
        progress.setProgressPercentage(progressPercentage);

        // Check if target achieved
        if (progressPercentage >= 100 && !progress.getIsAchieved()) {
            progress.setIsAchieved(true);
            progress.setAchievedDate(LocalDateTime.now());
        }

        progress.setUpdatedAt(LocalDateTime.now());
        DriverBonusProgress savedProgress = driverBonusProgressRepository.save(progress);

        return mapToDTO(savedProgress);
    }

    private double calculateProgressPercentage(DriverBonusProgress progress, BonusConfiguration config) {
        double tripProgress = 0;
        double revenueProgress = 0;

        // Calculate trip progress if target exists
        if (config.getTargetTrips() != null && config.getTargetTrips() > 0) {
            tripProgress = (double) progress.getCompletedTrips() / config.getTargetTrips() * 100;
        }

        // Calculate revenue progress if target exists
        if (config.getRevenueTarget() != null && config.getRevenueTarget() > 0) {
            revenueProgress = progress.getCurrentRevenue() / config.getRevenueTarget() * 100;
        }

        // If both targets exist, must meet both requirements - take the minimum progress
        if (config.getTargetTrips() != null && config.getRevenueTarget() != null) {
            return Math.min(tripProgress, revenueProgress);
        } else if (config.getTargetTrips() != null) {
            return tripProgress;
        } else if (config.getRevenueTarget() != null) {
            return revenueProgress;
        }

        return 0;
    }

    public List<DriverBonusProgressDTO> getPendingRewards() {
        return driverBonusProgressRepository.findByIsAchievedTrueAndIsRewardedFalse()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public DriverBonusProgressDTO approveReward(Long progressId) {
        DriverBonusProgress progress = driverBonusProgressRepository.findById(progressId)
                .orElseThrow(() -> new BadRequestException("Driver Progress not found"));
        if(progress.getProgressPercentage() < 100)
        {
            throw new BadRequestException("Driver does not complete this bonus!");
        }
        if(progress.getIsRewarded()){
            throw new BadRequestException("You have already completed this progress!");
        }
        Account account = accountServiceClient.getAccountById(progress.getAccountId());
        BonusConfiguration bonusConfiguration = bonusConfigurationRepository.findById(progress.getBonusConfigId()).
                orElseThrow(() -> new BadRequestException("Bonus Configuration not found"));

        Double bonusAmount = bonusConfiguration.getBonusAmount();
        account.setBalance(account.getBalance() + bonusAmount);
        // Update account through client


        progress.setIsRewarded(true);
        progress.setRewardedDate(LocalDateTime.now());
        progress.setUpdatedAt(LocalDateTime.now());
        accountRepository.save(account);
        DriverBonusProgress updatedProgress = driverBonusProgressRepository.save(progress);

        // Record the balance history for this bonus payment
        balanceHistoryService.recordBonus(progressId, progress.getAccountId(), bonusAmount);

        return mapToDTO(updatedProgress);
    }

    public DriverBonusProgressDTO getEligibleBonusForDriver(Integer accountId) {
        // Get current month
        int currentMonth = LocalDateTime.now().getMonthValue();

        // Check if driver already has an active bonus for this month
        Optional<DriverBonusProgress> existingMonthlyBonus =
                driverBonusProgressRepository.findByAccountIdAndBonusMonth(
                        accountId, currentMonth);

        if (existingMonthlyBonus.isPresent()) {
            // Driver already has a bonus for this month
            return mapToDTO(existingMonthlyBonus.get());
        }

        // If no existing bonus, proceed with normal eligibility logic
        Account driverAccount = accountServiceClient.getAccountById(accountId);
        DriverGroup driverGroup = determineDriverGroup(driverAccount);
        BonusTier bonusTier = determineBonusTier(driverAccount);

        Optional<BonusConfiguration> eligibleConfigOpt = bonusConfigurationRepository
                .findActiveConfigurationForDriverGroupAndTier(driverGroup, bonusTier, LocalDateTime.now());

        if (eligibleConfigOpt.isEmpty()) {
            return null; // No active bonus for this driver
        }

        BonusConfiguration eligibleConfig = eligibleConfigOpt.get();

        // Get or create progress record WITH month data
        return getOrCreateProgressRecord(accountId, eligibleConfig.getBonusConfigurationId(), currentMonth);
    }


    private DriverGroup determineDriverGroup(Account account) {
        // Implement your logic to determine if the driver is NEWBIE or REGULAR
        // For example:
        if (account.getRanking() == Rank.BRONZE && account.getLoyaltyPoints() < 2500) {
            return DriverGroup.NEWBIE;
        } else {
            return DriverGroup.REGULAR;
        }
    }

    public BonusTier determineBonusTier(Account account) {
        switch (account.getRanking()) {
            case BRONZE:
                return BonusTier.TIER1;
            case SILVER:
                return BonusTier.TIER2;
            case GOLD:
                return BonusTier.TIER3;
            case PLATINUM:
                return BonusTier.TIER4;
            default:
                return BonusTier.TIER1;  // Default to the lowest tier
        }
    }

    private DriverBonusProgressDTO getOrCreateProgressRecord(Integer accountId, Long bonusConfigId,
                                                             Integer bonusMonth) {
        Optional<DriverBonusProgress> existingProgress =
                driverBonusProgressRepository.findByAccountIdAndBonusConfigId(accountId, bonusConfigId);

        if (existingProgress.isPresent()) {
            DriverBonusProgress progress = existingProgress.get();
            if (progress.getBonusMonth() == null) {
                progress.setBonusMonth(bonusMonth);
                progress = driverBonusProgressRepository.save(progress);
            }
            return mapToDTO(progress);
        } else {
            // Create new progress record with month
            DriverBonusProgress newProgress = DriverBonusProgress.builder()
                    .accountId(accountId)
                    .bonusConfigId(bonusConfigId)
                    .completedTrips(0)
                    .currentRevenue(0.0)
                    .progressPercentage(0.0)
                    .isAchieved(false)
                    .isRewarded(false)
                    .bonusMonth(bonusMonth)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            DriverBonusProgress savedProgress = driverBonusProgressRepository.save(newProgress);
            return mapToDTO(savedProgress);
        }
    }

    private DriverBonusProgressDTO mapToDTO(DriverBonusProgress progress) {
        BonusConfiguration bonusConfig = bonusConfigurationRepository.findById(progress.getBonusConfigId())
                .orElseThrow(() -> new RuntimeException("Bonus Configuration not found"));

        DriverBonusProgressDTO dto = DriverBonusProgressDTO.builder()
                .driverBonusProgressId(progress.getDriverBonusProgressId())
                .accountId(progress.getAccountId())
                .bonusConfigId(progress.getBonusConfigId())
                .completedTrips(progress.getCompletedTrips())
                .currentRevenue(progress.getCurrentRevenue())
                .progressPercentage(progress.getProgressPercentage())
                .isAchieved(progress.getIsAchieved())
                .achievedDate(progress.getAchievedDate())
                .isRewarded(progress.getIsRewarded())
                .rewardedDate(progress.getRewardedDate())
                .createdAt(progress.getCreatedAt())
                .updatedAt(progress.getUpdatedAt())
                .build();

        // Add additional status fields
        boolean tripRequirementMet = (bonusConfig.getTargetTrips() == null) ||
                (progress.getCompletedTrips() >= bonusConfig.getTargetTrips());
        boolean revenueRequirementMet = (bonusConfig.getRevenueTarget() == null) ||
                (progress.getCurrentRevenue() >= bonusConfig.getRevenueTarget());

        dto.setTripRequirementMet(tripRequirementMet);
        dto.setRevenueRequirementMet(revenueRequirementMet);

        if (progress.getIsRewarded()) {
            dto.setBonusStatus("Rewarded");
        } else if (progress.getIsAchieved()) {
            dto.setBonusStatus("Achieved - Pending Reward");
        } else if (tripRequirementMet && !revenueRequirementMet) {
            dto.setBonusStatus("Trip Target Met - Revenue Target Pending");
        } else if (!tripRequirementMet && revenueRequirementMet) {
            dto.setBonusStatus("Revenue Target Met - Trip Target Pending");
        } else {
            dto.setBonusStatus("In Progress");
        }

        // Add the bonus configuration details if needed
        try {
            dto.setBonusConfiguration(bonusConfigurationService.getBonusConfigurationById(progress.getBonusConfigId()));
        } catch (Exception e) {
            // Log error but continue
            System.err.println("Could not fetch bonus configuration: " + e.getMessage());
        }

        // Add the driver account details if needed
        try {
            Account driverAccount = accountServiceClient.getAccountById(progress.getAccountId());
            dto.setDriverAccount(mapToAccountDTO(driverAccount));
        } catch (Exception e) {
            System.err.println("Could not fetch driver account: " + e.getMessage());
        }

        return dto;
    }

    private AccountDTO mapToAccountDTO(Account account) {
        return AccountDTO.builder()
                .accountId(account.getAccountId())
                .username(account.getUsername())
                .fullName(account.getFullName())
                .email(account.getEmail())
                .phone(account.getPhone())
                .profilePicture(account.getProfilePicture())
                .status(account.getStatus())
                .balance(account.getBalance())
                .ranking(account.getRanking())
                .loyaltyPoints(account.getLoyaltyPoints())
                .build();
    }
}
