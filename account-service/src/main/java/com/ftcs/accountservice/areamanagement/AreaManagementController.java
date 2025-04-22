package com.ftcs.accountservice.areamanagement;

import com.ftcs.accountservice.AccountURL;
import com.ftcs.accountservice.areamanagement.dto.AccountResponseDTO;
import com.ftcs.accountservice.areamanagement.dto.AreaManagementRequestDTO;
import com.ftcs.accountservice.areamanagement.service.AreaManagementService;
import com.ftcs.accountservice.driver.identification.model.AddressDriver;
import com.ftcs.accountservice.driver.identification.model.DriverIdentification;
import com.ftcs.accountservice.driver.management.dto.ListDriverDTO;
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

    @PostMapping("/addNewArea/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<?> addNewArea(@Valid @RequestBody AreaManagementRequestDTO requestDTO,
                                     @PathVariable("accountId") Integer accountId) {
        areaManagementService.addNewArea(accountId, requestDTO);
        return new ApiResponse<>("Area added successfully");
    }

    @PutMapping("/editArea")
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<?> editArea(@Valid @RequestBody AreaManagementRequestDTO updatedRequestDTO,
                                   @RequestAttribute("accountId") Integer accountId) {
        areaManagementService.updateAreas(accountId, updatedRequestDTO);
        return new ApiResponse<>("Area updated successfully");
    }

    @DeleteMapping("/deleteArea/{provinceId}")
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<?> deleteArea(@PathVariable("provinceId") Integer provinceId,
                                     @RequestAttribute("accountId") Integer accountId) {
        areaManagementService.deleteArea(accountId, provinceId);
        return new ApiResponse<>("Area deleted successfully");
    }

    @GetMapping("/driver")
    @PreAuthorize("hasPermission(null, 'AREA_MANAGEMENT')")
    public ApiResponse<List<ListDriverDTO>> getDriversByAccountId(@RequestAttribute("accountId") Integer accountId) {
        List<ListDriverDTO> driverDetails = areaManagementService.getAllDriverDetails(accountId);
        return new ApiResponse<>(driverDetails);
    }

    @GetMapping("/getProvincesByAccountId/{accountId}")
    @PreAuthorize("hasPermission(null, 'AREA_MANAGEMENT') or hasPermission(null, 'ADMIN')")
    public ApiResponse<?> getProvincesByAccountId(@PathVariable("accountId") Integer accountId) {
        List<Integer> provinces = areaManagementService.getProvincesByAccountId(accountId);
        return new ApiResponse<>(provinces);
    }
}
