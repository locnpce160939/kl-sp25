package com.ftcs.accountservice.driver.verification;

import com.ftcs.accountservice.AccountURL;
import com.ftcs.accountservice.driver.verification.dto.VerifiedDocumentRequestDTO;
import com.ftcs.accountservice.driver.verification.service.VerificationDriverService;
import com.ftcs.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AccountURL.DRIVER_REGISTER)
public class VerificationDriverController {

    private final VerificationDriverService verificationDriverService;

    @GetMapping("/checkRequiredInformation")
    @PreAuthorize("hasPermission(null, 'DRIVER')")
    public ApiResponse<?> checkRequiredInformation(@Valid @RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(verificationDriverService.validateRequiredInformation(accountId));
    }

    @PutMapping("/verifiedDocument/{accountId}")
    @PreAuthorize("hasPermission(null, 'HR')")
    public ApiResponse<?> updateVerificationStatus(@Valid @RequestBody VerifiedDocumentRequestDTO requestDTO,
                                                   @PathVariable("accountId") Integer accountId){
        verificationDriverService.updateVerificationStatus(accountId, requestDTO);
        return new ApiResponse<>("Updated verification status successfully");
    }
}