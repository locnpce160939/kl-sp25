package com.ftcs.accountservice.driver.identification;

import com.ftcs.accountservice.AccountURL;
import com.ftcs.accountservice.driver.identification.dto.DriverIdentificationRequestDTO;
import com.ftcs.accountservice.driver.identification.service.IdentificationDriverService;
import com.ftcs.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AccountURL.DRIVER_REGISTER)
public class IdentificationDriverController {

    private final IdentificationDriverService identificationDriverService;

    @PostMapping("/createDriverIdentification")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> createDriverIdentification(@Valid @RequestBody DriverIdentificationRequestDTO requestDTO,
                                                     @RequestAttribute("accountId") Integer accountId) {
        identificationDriverService.addDriverIdentification(requestDTO, accountId);
        return new ApiResponse<>("Created driver identification successfully");
    }

    @PutMapping("/updateDriverIdentification/{driverIdentificationId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> updateDriverIdentification(@Valid @RequestBody DriverIdentificationRequestDTO requestDTO,
                                                     @PathVariable("driverIdentificationId") Integer driverIdentificationId,
                                                     @RequestAttribute("accountId") Integer accountId) {
        identificationDriverService.updateDriverIdentification(requestDTO, driverIdentificationId, accountId);
        return new ApiResponse<>("Updated driver identification successfully");
    }

    @PutMapping("/updateDriverIdentification")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> updateDriverIdentificationByAccountId(@Valid @RequestBody DriverIdentificationRequestDTO requestDTO,
                                                     @RequestAttribute("accountId") Integer accountId) {
        identificationDriverService.updateDriverIdentificationByAccountId(requestDTO, accountId);
        return new ApiResponse<>("Updated driver identification successfully");
    }

    @GetMapping("/identification/getById/{driverIdentificationId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> getById(@PathVariable("driverIdentificationId") Integer driverIdentificationId) {
        return new ApiResponse<>(identificationDriverService.findDriverIdentificationByDriverIdentificationId(driverIdentificationId));
    }

    @GetMapping("/identification/getByAccountId")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> getByAccountId(@RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(identificationDriverService.findDriverIdentificationByAccountId(accountId));
    }
}