package com.ftcs.accountservice.driver.license;

import com.ftcs.accountservice.AccountURL;
import com.ftcs.accountservice.driver.license.dto.LicenseRequestDTO;
import com.ftcs.accountservice.driver.license.service.LicenseDriverService;
import com.ftcs.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AccountURL.DRIVER_REGISTER)
public class LicenseDriverController {

    private final LicenseDriverService licenseDriverService;

    @PostMapping("/createNewLicense")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> createNewLicense(@Valid @RequestBody LicenseRequestDTO requestDTO,
                                           @RequestAttribute("accountId") Integer accountId) {
        licenseDriverService.createNewLicense(requestDTO, accountId);
        return new ApiResponse<>("Created license successfully");
    }

    @PutMapping("/updateLicense/{licenseId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> updateLicense(@Valid @RequestBody LicenseRequestDTO requestDTO,
                                        @PathVariable("licenseId") Integer licenseId,
                                        @RequestAttribute("accountId") Integer accountId) {
        licenseDriverService.updateLicense(accountId, requestDTO, licenseId);
        return new ApiResponse<>("Updated license successfully");
    }

    @GetMapping("/license/getById/{licenseId}")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> getLicenseById(@PathVariable("licenseId") Integer licenseId) {
        return new ApiResponse<>(licenseDriverService.findLicenseByLicenseId(licenseId));
    }

}