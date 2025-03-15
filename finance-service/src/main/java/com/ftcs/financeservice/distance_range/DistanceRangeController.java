package com.ftcs.financeservice.distance_range;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.financeservice.FinanceURL;
import com.ftcs.financeservice.distance_range.dto.DistanceRangeRequestDTO;
import com.ftcs.financeservice.distance_range.model.DistanceRange;
import com.ftcs.financeservice.distance_range.service.DistanceRangeService;
import com.ftcs.financeservice.holiday_surcharge.model.HolidaySurcharge;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(FinanceURL.DISTANCE_RANGE)
public class DistanceRangeController {
    private final DistanceRangeService distanceRangeService;

    @GetMapping
    @PreAuthorize("hasPermission(null, 'FINANCE') or hasPermission(null, 'ADMIN')")
    public ApiResponse<?> getAllDistanceRages(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                              @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<DistanceRange> distanceRanges = distanceRangeService.getAllDistanceRanges(page,size);
        return new ApiResponse<>("Fetched distance rages surcharges successfully.", distanceRanges);
    }

    @GetMapping("/{distanceRangeId}")
    @PreAuthorize("hasPermission(null, 'FINANCE') or hasPermission(null, 'ADMIN')")
    public ApiResponse<?> getDistanceRangeById(@PathVariable("distanceRangeId") Integer distanceRangeId) {
        return new ApiResponse<>(distanceRangeService.findByDistanceId(distanceRangeId));
    }

    @PostMapping
    @PreAuthorize("hasPermission(null, 'FINANCE') or hasPermission(null, 'ADMIN')")
    public ApiResponse<?> createDistanceRange(@Valid @RequestBody DistanceRangeRequestDTO requestDTO,
                                              @RequestAttribute("accountId") Integer accountId) {
        distanceRangeService.createDistanceRange(accountId, requestDTO);
        return new ApiResponse<>("Created distance range successfully.");
    }

    @PutMapping("/{distanceRageId}")
    @PreAuthorize("hasPermission(null, 'FINANCE') or hasPermission(null, 'ADMIN')")
    public ApiResponse<?> update(@Valid @RequestBody DistanceRangeRequestDTO requestDTO,
                                 @RequestAttribute("accountId") Integer accountId,
                                 @PathVariable("distanceRageId") Integer distanceRageId) {
        distanceRangeService.updateDistanceRange(accountId, distanceRageId, requestDTO);
        return new ApiResponse<>("Updated distance range successfully.");
    }
}
