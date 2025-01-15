package com.ftcs.accountservice.driver.vehicle;

import com.ftcs.accountservice.AccountURL;
import com.ftcs.accountservice.driver.vehicle.dto.VehicleRequestDTO;
import com.ftcs.accountservice.driver.vehicle.service.VehicleDriverService;
import com.ftcs.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PutMapping("/updateVehicles")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> updateVehiclesByAccountId(@Valid @RequestBody List<VehicleRequestDTO> requestDTOs,
                                                    @RequestAttribute("accountId") Integer accountId) {
        vehicleDriverService.updateVehiclesByAccountId(accountId, requestDTOs);
        return new ApiResponse<>("Updated vehicles successfully");
    }

    @GetMapping("/vehicle/getById/{vehicleId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> getVehicleById(@PathVariable("vehicleId") Integer vehicleId) {
        return new ApiResponse<>(vehicleDriverService.findVehicleByVehicleId(vehicleId));
    }

    @GetMapping("/vehicles/getByAccountId")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> getVehiclesByAccountId(@RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(vehicleDriverService.findVehiclesByAccountId(accountId));
    }
}
