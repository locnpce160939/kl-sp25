package com.ftcs.bonusservice.service;

import com.ftcs.bonusservice.dto.BonusConfigurationCreateRequest;
import com.ftcs.bonusservice.dto.BonusConfigurationDTO;
import com.ftcs.bonusservice.model.BonusConfiguration;
import com.ftcs.bonusservice.repository.BonusConfigurationRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BonusConfigurationService {
    private final BonusConfigurationRepository bonusConfigurationRepository;

    public BonusConfigurationDTO createBonusConfiguration(BonusConfigurationCreateRequest request) {
        BonusConfiguration bonusConfiguration = BonusConfiguration.builder()
                .bonusName(request.getBonusName())
                .description(request.getDescription())
                .targetTrips(request.getTargetTrips())
                .revenueTarget(request.getRevenueTarget())
                .rewardType(request.getRewardType())
                .driverGroup(request.getDriverGroup())
                .bonusTier(request.getBonusTier())  // Add this line
                .bonusAmount(request.getBonusAmount())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        BonusConfiguration savedConfig = bonusConfigurationRepository.save(bonusConfiguration);
        return mapToDTO(savedConfig);
    }

    public Page<BonusConfigurationDTO> getAllBonusConfigurations(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BonusConfiguration> configPage = bonusConfigurationRepository.findAll(pageable);

        List<BonusConfigurationDTO> dtoList = configPage.getContent()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, configPage.getTotalElements());
    }

    public Page<BonusConfigurationDTO> getActiveBonusConfigurations(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BonusConfiguration> configPage =  bonusConfigurationRepository.findActiveConfigurations(LocalDateTime.now(), pageable);
        List<BonusConfigurationDTO> dtoList = configPage.getContent()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, configPage.getTotalElements());
    }

    public Page<BonusConfigurationDTO> getBonusConfigurationsByDriverGroup(BonusConfigurationCreateRequest driverGroup, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BonusConfiguration> configPage =  bonusConfigurationRepository.findByDriverGroup(driverGroup.getDriverGroup(), pageable);
        List<BonusConfigurationDTO> dtoList = configPage.getContent()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, configPage.getTotalElements());
    }

    public Page<BonusConfigurationDTO> getBonusConfigurationsByRewardType(BonusConfigurationCreateRequest rewardType, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BonusConfiguration> configPage = bonusConfigurationRepository.findByRewardType(rewardType.getRewardType(), pageable);
        List<BonusConfigurationDTO> dtoList = configPage.getContent()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
        return new PageImpl<>(dtoList, pageable, configPage.getTotalElements());
    }

    public BonusConfigurationDTO getBonusConfigurationById(Long id) {
        return bonusConfigurationRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new RuntimeException("Bonus Configuration not found with id: " + id));
    }

    public BonusConfigurationDTO updateBonusConfiguration(Long id, BonusConfigurationCreateRequest request) {
        BonusConfiguration configuration = bonusConfigurationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bonus Configuration not found with id: " + id));

        configuration.setBonusName(request.getBonusName());
        configuration.setDescription(request.getDescription());
        configuration.setTargetTrips(request.getTargetTrips());
        configuration.setRevenueTarget(request.getRevenueTarget());
        configuration.setRewardType(request.getRewardType());
        configuration.setDriverGroup(request.getDriverGroup());
        configuration.setBonusTier(request.getBonusTier());  // Add this line
        configuration.setBonusAmount(request.getBonusAmount());
        configuration.setStartDate(request.getStartDate());
        configuration.setEndDate(request.getEndDate());
        configuration.setUpdatedAt(LocalDateTime.now());

        BonusConfiguration updatedConfig = bonusConfigurationRepository.save(configuration);
        return mapToDTO(updatedConfig);
    }

    public void deleteBonusConfiguration(Long id) {
        bonusConfigurationRepository.deleteById(id);
    }

    public BonusConfigurationDTO deactivateBonusConfiguration(Long id) {
        BonusConfiguration configuration = bonusConfigurationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Bonus Configuration not found with id: " + id));

        configuration.setIsActive(false);
        configuration.setUpdatedAt(LocalDateTime.now());

        BonusConfiguration updatedConfig = bonusConfigurationRepository.save(configuration);
        return mapToDTO(updatedConfig);
    }

    private BonusConfigurationDTO mapToDTO(BonusConfiguration bonusConfiguration) {
        return BonusConfigurationDTO.builder()
                .bonusConfigurationId(bonusConfiguration.getBonusConfigurationId())
                .bonusName(bonusConfiguration.getBonusName())
                .description(bonusConfiguration.getDescription())
                .targetTrips(bonusConfiguration.getTargetTrips())
                .revenueTarget(bonusConfiguration.getRevenueTarget())
                .rewardType(bonusConfiguration.getRewardType())
                .driverGroup(bonusConfiguration.getDriverGroup())
                .bonusTier(bonusConfiguration.getBonusTier())  // Add this line
                .bonusAmount(bonusConfiguration.getBonusAmount())
                .startDate(bonusConfiguration.getStartDate())
                .endDate(bonusConfiguration.getEndDate())
                .isActive(bonusConfiguration.getIsActive())
                .createdAt(bonusConfiguration.getCreatedAt())
                .updatedAt(bonusConfiguration.getUpdatedAt())
                .build();
    }
}
