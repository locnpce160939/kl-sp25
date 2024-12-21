package com.ftcs.accountservice.driver.vehicle;

import com.ftcs.accountservice.AccountURL;
import com.ftcs.accountservice.driver.vehicle.dto.VehicleRequestDTO;
import com.ftcs.accountservice.driver.vehicle.service.VehicleDriverService;
import com.ftcs.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AccountURL.DRIVER_REGISTER)
public class VehicleDriverController {

    private final VehicleDriverService vehicleDriverService;

    @PostMapping("/createNewVehicle")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> createNewVehicle(@Valid @RequestBody VehicleRequestDTO requestDTO,
                                           @RequestAttribute("accountId") Integer accountId) {
        vehicleDriverService.createNewVehicle(requestDTO, accountId);
        return new ApiResponse<>("Created vehicle successfully");
    }

    @PutMapping("/updateVehicle/{vehicleId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> updateVehicle(@Valid @RequestBody VehicleRequestDTO requestDTO,
                                        @RequestAttribute("accountId") Integer accountId,
                                        @PathVariable("vehicleId") Integer vehicleId) {
        vehicleDriverService.updateVehicle(accountId, requestDTO, vehicleId);
        return new ApiResponse<>("Updated vehicle successfully");
    }

    @GetMapping("/getById/{vehicleId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> getVehicleById(@PathVariable("vehicleId") Integer vehicleId) {
        vehicleDriverService.findVehicleByVehicleId(vehicleId);
        return new ApiResponse<>("Vehicle successfully found");
    }

}