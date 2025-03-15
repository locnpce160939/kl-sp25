package com.ftcs.financeservice.holiday_surcharge;
import com.ftcs.common.dto.ApiResponse;
import com.ftcs.financeservice.FinanceURL;
import com.ftcs.financeservice.holiday_surcharge.dto.HolidaySurchargeRequestDTO;
import com.ftcs.financeservice.holiday_surcharge.model.HolidaySurcharge;
import com.ftcs.financeservice.holiday_surcharge.service.HolidaySurchargeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequiredArgsConstructor
@RequestMapping(FinanceURL.HOLIDAY_SURCHARGE)
public class HolidaySurchargeController {

    private final HolidaySurchargeService holidaySurchargeService;

    @PostMapping()
    @PreAuthorize("hasPermission(null, 'FINANCE') or hasPermission(null, 'ADMIN')")
    public ApiResponse<?> createHolidaySurcharge(@Valid @RequestBody HolidaySurchargeRequestDTO requestDTO,
                                                 @RequestAttribute("accountId") Integer accountId) {
        holidaySurchargeService.createHolidaySurcharge(accountId, requestDTO);
        return new ApiResponse<>("Holiday Surcharge created successfully.");
    }

    @GetMapping
    @PreAuthorize("hasPermission(null, 'FINANCE') or hasPermission(null, 'ADMIN')")
    public ApiResponse<?> getAllHolidaySurcharges(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                  @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<HolidaySurcharge> holidaySurcharges = holidaySurchargeService.getAllHolidaySurcharges(page, size);
        return new ApiResponse<>("Fetched holiday surcharges successfully.", holidaySurcharges);
    }

    @GetMapping("/{holidaySurchargeId}")
    @PreAuthorize("hasPermission(null, 'FINANCE') or hasPermission(null, 'ADMIN')")
    public ApiResponse<?> getHolidaySurchargeById(@PathVariable("holidaySurchargeId") Integer holidaySurchargeId) {
        return new ApiResponse<>(holidaySurchargeService.getHolidaySurchargeById(holidaySurchargeId));
    }

    @PutMapping("/{holidaySurchargeId}")
    @PreAuthorize("hasPermission(null, 'FINANCE') or hasPermission(null, 'ADMIN')")
    public ApiResponse<?> updateHolidaySurcharge(@Valid @PathVariable("holidaySurchargeId") Integer holidaySurchargeId, @RequestBody HolidaySurchargeRequestDTO requestDTO,
                                                 @RequestAttribute("accountId") Integer accountId) {
        holidaySurchargeService.updateHolidaySurcharge(accountId, holidaySurchargeId, requestDTO);
        return new ApiResponse<>("Holiday Surcharge updated successfully.");
    }

    @DeleteMapping("/{holidaySurchargeId}")
    @PreAuthorize("hasPermission(null, 'FINANCE') or hasPermission(null, 'ADMIN')")
    public ApiResponse<?> deleteHolidaySurcharge(@PathVariable("holidaySurchargeId") Integer holidaySurchargeId) {
        holidaySurchargeService.deleteHolidaySurcharge(holidaySurchargeId);
        return new ApiResponse<>("Holiday Surcharge deleted successfully.");
    }
}

