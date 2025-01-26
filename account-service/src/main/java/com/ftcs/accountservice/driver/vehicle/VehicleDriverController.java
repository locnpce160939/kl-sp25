package com.ftcs.accountservice.driver.vehicle;

import com.ftcs.accountservice.AccountURL;
import com.ftcs.accountservice.driver.vehicle.dto.VehicleRequestDTO;
import com.ftcs.accountservice.driver.vehicle.service.VehicleDriverService;
import com.ftcs.common.dto.ApiResponse;
import com.ftcs.common.upload.FolderEnum;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(AccountURL.DRIVER_REGISTER)
public class VehicleDriverController {

    private final VehicleDriverService vehicleDriverService;

    @PostMapping("/vehicle")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> createNewVehicle(@RequestPart("requestDTO") VehicleRequestDTO requestDTO,
                                           @RequestPart("frontFile") MultipartFile frontFile,
                                           @RequestPart("backFile") MultipartFile backFile,
                                           @RequestAttribute("accountId") Integer accountId) {
        vehicleDriverService.createNewVehicle(requestDTO, accountId, frontFile, backFile);
        return new ApiResponse<>("Created vehicle successfully");
    }

    @PutMapping("/vehicle")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> updateVehicle(@RequestPart("requestDTO") VehicleRequestDTO requestDTO,
                                        @RequestPart(value = "frontFile", required = false) MultipartFile frontFile,
                                        @RequestPart(value = "backFile", required = false) MultipartFile backFile,
                                        @RequestAttribute("accountId") Integer accountId) {
        vehicleDriverService.updateVehicle(requestDTO, accountId, frontFile, backFile);
        return new ApiResponse<>("Updated vehicle successfully");
    }

    @GetMapping("/vehicle/{vehicleId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> getVehicleById(@PathVariable("vehicleId") Integer vehicleId) {
        return new ApiResponse<>(vehicleDriverService.findVehicleByVehicleId(vehicleId));
    }

    @GetMapping("/vehicle")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> getVehiclesByAccountId(@RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(vehicleDriverService.findVehiclesByAccountId(accountId));
    }
}
