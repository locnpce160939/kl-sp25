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

    @PostMapping("/areas/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'AREA_MANAGEMENT')")
    public ApiResponse<?> addAreas(@Valid @RequestBody AreaManagementRequestDTO requestDTO,
                                   @PathVariable("accountId") Integer accountId) {
        areaManagementService.addNewArea(accountId, requestDTO);
        return new ApiResponse<>("Areas added successfully");
    }

    @PutMapping("/areas/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'AREA_MANAGEMENT')")
    public ApiResponse<?> updateAreas(@Valid @RequestBody AreaManagementRequestDTO requestDTO,
                                     @PathVariable("accountId") Integer accountId) {
        areaManagementService.updateAreas(accountId, requestDTO);
        return new ApiResponse<>("Areas updated successfully");
    }

    @DeleteMapping("/areas/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'AREA_MANAGEMENT')")
    public ApiResponse<?> deleteAreas(@PathVariable("accountId") Integer accountId,
                                    @RequestBody AreaManagementRequestDTO requestDTO) {
        areaManagementService.deleteAreas(accountId, requestDTO.getProvinceIds());
        return new ApiResponse<>("Areas deleted successfully");
    }

    @GetMapping("/drivers")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'AREA_MANAGEMENT')")
    public ApiResponse<List<ListDriverDTO>> getDriversByAccountId(@RequestAttribute("accountId") Integer accountId) {
        List<ListDriverDTO> driverDetails = areaManagementService.getAllDriverDetails(accountId);
        return new ApiResponse<>("Drivers retrieved successfully", driverDetails);
    }

    @GetMapping("/areas/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'AREA_MANAGEMENT')")
    public ApiResponse<?> getProvincesByAccountId(@PathVariable("accountId") Integer accountId) {
        List<Integer> provinces = areaManagementService.getProvincesByAccountId(accountId);
        return new ApiResponse<>("Provinces retrieved successfully", provinces);
    }
}
