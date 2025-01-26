package com.ftcs.accountservice.driver.identification;

import com.ftcs.accountservice.AccountURL;
import com.ftcs.accountservice.driver.identification.dto.DriverIdentificationRequestDTO;
import com.ftcs.accountservice.driver.identification.service.DriverIdentificationService;
import com.ftcs.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping(AccountURL.DRIVER_REGISTER)
public class IdentificationDriverController {

    private final DriverIdentificationService driverIdentificationService;

    @PostMapping("/identification")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> createDriverIdentification(@Valid @RequestPart("requestDTO") DriverIdentificationRequestDTO requestDTO,
                                                     @RequestPart("frontFile") MultipartFile frontFile,
                                                     @RequestPart("backFile") MultipartFile backFile,
                                                     @RequestAttribute("accountId") Integer accountId) {
        driverIdentificationService.addDriverIdentification(requestDTO, accountId, frontFile, backFile);
        return new ApiResponse<>("Created driver identification successfully");
    }

    @PutMapping("/identification")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> updateDriverIdentificationByAccountId(@Valid @RequestPart("requestDTO") DriverIdentificationRequestDTO requestDTO,
                                                                @RequestPart(value = "frontFile", required = false) MultipartFile frontFile,
                                                                @RequestPart(value = "backFile", required = false) MultipartFile backFile,
                                                                @RequestAttribute("accountId") Integer accountId) {
        driverIdentificationService.updateDriverIdentification(requestDTO, accountId, frontFile, backFile);
        return new ApiResponse<>("Updated driver identification successfully");
    }

    @GetMapping("/identification/{driverIdentificationId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> getById(@PathVariable("driverIdentificationId") Integer driverIdentificationId) {
        return new ApiResponse<>(driverIdentificationService.findDriverIdentificationByDriverIdentificationId(driverIdentificationId));
    }

    @GetMapping("/identification")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> getByAccountId(@RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(driverIdentificationService.findDriverIdentification(accountId));
    }
}