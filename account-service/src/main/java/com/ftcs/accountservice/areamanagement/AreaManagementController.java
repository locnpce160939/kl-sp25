package com.ftcs.accountservice.areamanagement;

import com.ftcs.accountservice.AccountURL;
import com.ftcs.accountservice.areamanagement.dto.AccountResponseDTO;
import com.ftcs.accountservice.areamanagement.dto.AreaManagementRequestDTO;
import com.ftcs.accountservice.areamanagement.service.AreaManagementService;
import com.ftcs.accountservice.driver.identification.model.AddressDriver;
import com.ftcs.accountservice.driver.identification.model.DriverIdentification;
import com.ftcs.authservice.features.account.Account;
import com.ftcs.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(AccountURL.AREA_MANAGEMENT)
public class AreaManagementController {

    private final AreaManagementService areaManagementService;

    @PostMapping("/addNewArea")
    @PreAuthorize("hasPermission(null, 'AREA_MANAGEMENT')") // Chỉnh quyền theo nhu cầu
    public ApiResponse<?> addNewArea(@Valid @RequestBody AreaManagementRequestDTO requestDTO,
                                     @RequestAttribute("accountId") Integer accountId) {
        areaManagementService.addNewArea(accountId, requestDTO);
        return new ApiResponse<>("Area added successfully");
    }

    @PutMapping("/editArea/{provinceId}")
    @PreAuthorize("hasPermission(null, 'AREA_MANAGEMENT')")
    public ApiResponse<?> editArea(@Valid @RequestBody AreaManagementRequestDTO updatedRequestDTO,
                                   @PathVariable("provinceId") Integer provinceId,
                                   @RequestAttribute("accountId") Integer accountId) {
        areaManagementService.editArea(accountId, provinceId, updatedRequestDTO);
        return new ApiResponse<>("Area updated successfully");
    }

    @DeleteMapping("/deleteArea/{provinceId}")
    @PreAuthorize("hasPermission(null, 'AREA_MANAGEMENT')")
    public ApiResponse<?> deleteArea(@PathVariable("provinceId") Integer provinceId,
                                     @RequestAttribute("accountId") Integer accountId) {
        areaManagementService.deleteArea(accountId, provinceId);
        return new ApiResponse<>("Area deleted successfully");
    }

    @GetMapping("/driver")
    @PreAuthorize("hasPermission(null, 'AREA_MANAGEMENT')")
    public ApiResponse<List<AccountResponseDTO>> getDriversByAccountId(@RequestAttribute("accountId") Integer accountId) {
        List<AccountResponseDTO> driverAccounts = areaManagementService.getDriverIdentificationsByAccountId(accountId);
        return new ApiResponse<>(driverAccounts);
    }

    @GetMapping("/getProvincesByAccountId")
    @PreAuthorize("hasPermission(null, 'AREA_MANAGEMENT')")
    public ApiResponse<?> getProvincesByAccountId(@RequestAttribute("accountId") Integer accountId) {
        List<Integer> provinces = areaManagementService.getProvincesByAccountId(accountId);
        return new ApiResponse<>(provinces);
    }
}
