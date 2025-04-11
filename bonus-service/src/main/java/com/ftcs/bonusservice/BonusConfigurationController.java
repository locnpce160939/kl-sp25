package com.ftcs.bonusservice;

import com.ftcs.bonusservice.dto.BonusConfigurationCreateRequest;
import com.ftcs.bonusservice.dto.BonusConfigurationDTO;
import com.ftcs.bonusservice.service.BonusConfigurationService;
import com.ftcs.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(BonusURL.BONUS_CONFIGURATION)
public class BonusConfigurationController {

    private final BonusConfigurationService bonusConfigurationService;

    @PostMapping
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<BonusConfigurationDTO> create (@Valid @RequestBody BonusConfigurationCreateRequest requestDTO) {
        return new ApiResponse<>(bonusConfigurationService.createBonusConfiguration(requestDTO));
    }

    @GetMapping
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<Page<BonusConfigurationDTO>> getAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                           @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(bonusConfigurationService.getAllBonusConfigurations(page, size));
    }

    @GetMapping("/active")
    public ApiResponse<Page<BonusConfigurationDTO>> getActive(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                              @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(bonusConfigurationService.getActiveBonusConfigurations(page, size));
    }

    @GetMapping("/driverGroup")
    public ApiResponse<Page<BonusConfigurationDTO>> getDriverGroup(@Valid @RequestBody BonusConfigurationCreateRequest request,
                                                                   @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                   @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(bonusConfigurationService.getBonusConfigurationsByDriverGroup(request, page, size));
    }

    @GetMapping("/rewardType")
    public ApiResponse<Page<BonusConfigurationDTO>> getRewardType(@Valid @RequestBody BonusConfigurationCreateRequest request,
                                                                   @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                   @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(bonusConfigurationService.getBonusConfigurationsByRewardType(request, page, size));
    }

    @PutMapping("/{bonusConfigurationId}")
    public ApiResponse<BonusConfigurationDTO> updateBonusConfigurationId(@PathVariable("bonusConfigurationId") Long bonusConfigurationId,
                                                                         @Valid @RequestBody BonusConfigurationCreateRequest request) {
        return new ApiResponse<>(bonusConfigurationService.updateBonusConfiguration(bonusConfigurationId, request));
    }

    @DeleteMapping("/{bonusConfigurationId}")
    public ApiResponse<?> deleteBonusConfiguration(@PathVariable("bonusConfigurationId") Long bonusConfigurationId) {
        bonusConfigurationService.deleteBonusConfiguration(bonusConfigurationId);
        return new ApiResponse<>("Delete Bonus Configuration Success");
    }

    @PutMapping("/deActive/{bonusConfigurationId}")
    public ApiResponse<BonusConfigurationDTO> deActivateBonusConfiguration(@PathVariable("bonusConfigurationId") Long bonusConfigurationId) {
        return new ApiResponse<>(bonusConfigurationService.deactivateBonusConfiguration(bonusConfigurationId));
    }
}
