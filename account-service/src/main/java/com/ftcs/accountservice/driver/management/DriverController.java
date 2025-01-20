package com.ftcs.accountservice.driver.management;

import com.ftcs.accountservice.AccountURL;
import com.ftcs.accountservice.driver.management.service.DriverService;
import com.ftcs.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(AccountURL.DRIVER)
public class DriverController {
    private final DriverService driverService;

    @GetMapping()
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<?> getAllDrivers() {
        return new ApiResponse<>(driverService.getAllDriverDetails());
    }
}
