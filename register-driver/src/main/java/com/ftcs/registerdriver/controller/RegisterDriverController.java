package com.ftcs.registerdriver.controller;

import com.ftcs.accountservice.AccountURL;
import com.ftcs.common.dto.ApiResponse;
import com.ftcs.registerdriver.RegisterDriverURL;
import com.ftcs.registerdriver.dto.*;
import com.ftcs.registerdriver.service.RegisterDriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(RegisterDriverURL.REGISTERDRIVER)
public class RegisterDriverController {

    private final RegisterDriverService registerDriverService;

    @PostMapping("/createNewLicense")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> createNewLicense(@Valid @RequestBody LicenseRequestDTO requestDTO,
                                           @RequestAttribute("accountId") Integer accountId) {
        registerDriverService.createNewLicense(requestDTO, accountId);
        return new ApiResponse<>("Created license successfully");
    }

    @PutMapping("/updateLicense/{licenseId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> updateLicense(@Valid @RequestBody LicenseRequestDTO requestDTO,
                                        @PathVariable("licenseId") Integer licenseId,
                                        @RequestAttribute("accountId") Integer accountId) {
        registerDriverService.updateLicense(accountId, requestDTO, licenseId);
        return new ApiResponse<>("Updated license successfully");
    }

    @PostMapping("/createNewVehicle")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> createNewVehicle(@Valid @RequestBody VehicleRequestDTO requestDTO,
                                           @RequestAttribute("accountId") Integer accountId) {
        registerDriverService.createNewVehicle(requestDTO, accountId);
        return new ApiResponse<>("Created vehicle successfully");
    }

    @PutMapping("/updateVehicle/{vehicleId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> updateVehicle(@Valid @RequestBody VehicleRequestDTO requestDTO,
                                        @RequestAttribute("accountId") Integer accountId,
                                        @PathVariable("vehicleId") Integer vehicleId) {
        registerDriverService.updateVehicle(accountId, requestDTO, vehicleId);
        return new ApiResponse<>("Updated vehicle successfully");
    }

    @PostMapping("/createDriverIdentification")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> createDriverIdentification(@Valid @RequestBody DriverIdentificationRequestDTO requestDTO,
                                                     @RequestAttribute("accountId") Integer accountId) {
        registerDriverService.addDriverIdentification(requestDTO, accountId);
        return new ApiResponse<>("Created driver identification successfully");
    }

    @PutMapping("/updateDriverIdentification/{driverIdentificationId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> updateDriverIdentification(@Valid @RequestBody DriverIdentificationRequestDTO requestDTO,
                                                     @PathVariable("driverIdentificationId") Integer driverIdentificationId,
                                                     @RequestAttribute("accountId") Integer accountId) {
        registerDriverService.updateDriverIdentification(requestDTO, driverIdentificationId, accountId);
        return new ApiResponse<>("Updated driver identification successfully");
    }

    @GetMapping("/checkRequiredInformation")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> checkRequiredInformation(@Valid @RequestAttribute("accountId") Integer accountId) {
        registerDriverService.validateRequiredInformation(accountId);
        return new ApiResponse<>("Account has all required information");
    }

    @PutMapping("/verifiedDocument/{accountId}")
    @PreAuthorize("hasPermission(null, 'HR')")
    public ApiResponse<?> updateVerificationStatus(@Valid @RequestBody VerifiedDocumentRequestDTO requestDTO,
                                                   @PathVariable("accountId") Integer accountId){
        registerDriverService.updateVerificationStatus(accountId, requestDTO);
        return new ApiResponse<>("Updated verification status successfully");
    }
}