package com.ftcs.bonusservice.service;

import com.ftcs.bonusservice.dto.AnalyticsResponseDTO;
import com.ftcs.bonusservice.model.BonusConfiguration;
import com.ftcs.bonusservice.model.DriverBonusProgress;
import com.ftcs.bonusservice.repository.BonusConfigurationRepository;
import com.ftcs.bonusservice.repository.DriverBonusProgressRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AnalyticsService {
    private final BonusConfigurationRepository bonusConfigurationRepository;
    private final DriverBonusProgressRepository driverBonusProgressRepository;


//    public List<AnalyticsResponseDTO> getBonusConfigurationAnalytics() {
//        List<BonusConfiguration> configurations = bonusConfigurationRepository.findAll();
//        List<AnalyticsResponseDTO> analytics = new ArrayList<>();
//
//        for (BonusConfiguration config : configurations) {
//            List<DriverBonusProgress> allProgress =
//                    driverBonusProgressRepository.findByBonusConfigId(config.getBonusConfigurationId());
//
//            List<DriverBonusProgress> achievedProgress =
//                    driverBonusProgressRepository.findAchievedProgressByConfigId(config.getBonusConfigurationId());
//
//            long totalDrivers = allProgress.size();
//            long achievedDrivers = achievedProgress.size();
//            double achievementRate = totalDrivers > 0 ? (double) achievedDrivers / totalDrivers * 100 : 0;
//            double totalReward = achievedDrivers * config.getBonusAmount();
//
//            // Calculate average time to achievement
//            double avgCompletionTime = calculateAverageCompletionTime(achievedProgress, config);
//
//            AnalyticsResponseDTO analyticsDTO = AnalyticsResponseDTO.builder()
//                    .bonusConfigId(config.getBonusConfigurationId())
//                    .bonusName(config.getBonusName())
//                    .rewardType(config.getRewardType())
//                    .driverGroup(config.getDriverGroup())
//                    .totalDrivers(totalDrivers)
//                    .achievedDrivers(achievedDrivers)
//                    .achievementRate(achievementRate)
//                    .totalRewardAmount(totalReward)
//                    .averageCompletionTime(avgCompletionTime)
//                    .build();
//
//            analytics.add(analyticsDTO);
//        }
//
//        return analytics;
//    }

//    public Map<String, Long> getDriverTierDistribution(Long bonusConfigId) {
//        List<DriverBonusProgress> progressList = driverBonusProgressRepository.findByBonusConfigId(bonusConfigId);
//
//        return progressList.stream()
//                .collect(Collectors.groupingBy(DriverBonusProgress::getBonusTier, Collectors.counting()));
//    }

    private double calculateAverageCompletionTime(List<DriverBonusProgress> achievedProgress, BonusConfiguration config) {
        if (achievedProgress.isEmpty()) {
            return 0;
        }

        return achievedProgress.stream()
                .filter(p -> p.getAchievedDate() != null)
                .mapToDouble(p -> {
                    Duration duration = Duration.between(config.getStartDate(), p.getAchievedDate());
                    return duration.toDays();
                })
                .average()
                .orElse(0);
    }
}
