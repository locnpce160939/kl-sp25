package com.ftcs.bonusservice;

import com.ftcs.bonusservice.dto.DriverBonusProgressDTO;
import com.ftcs.bonusservice.service.DriverBonusProgressService;
import com.ftcs.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(BonusURL.DRIVER_BONUS_PROGRESS)
public class DriverBonusProgressController {
    private final DriverBonusProgressService driverBonusProgressService;


    @GetMapping
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<DriverBonusProgressDTO> getEligibleBonusForDriver(@RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(driverBonusProgressService.getEligibleBonusForDriver(accountId));
    }

    @PutMapping("/approvedReward/{driverBonusProgressId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<DriverBonusProgressDTO> approvedReward(@PathVariable("driverBonusProgressId") Long driverBonusProgressId) {
        return new ApiResponse<>(driverBonusProgressService.approveReward(driverBonusProgressId));
    }

    @GetMapping("/account/{accountId}")
    public ApiResponse<Page<DriverBonusProgressDTO>> getByAccountId(@PathVariable("accountId") Integer accountId,
                                                                    @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(driverBonusProgressService.getDriverProgressByAccountId(accountId,page,size));
    }

    @GetMapping("/bonusConfig/{bonusConfigId}")
    public ApiResponse<Page<DriverBonusProgressDTO>> getByBonusId(@PathVariable("bonusConfigId") Long bonusConfigId,
                                                                    @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                    @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(driverBonusProgressService.getProgressByBonusConfigId(bonusConfigId,page,size));
    }


}
