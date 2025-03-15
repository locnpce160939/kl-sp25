package com.ftcs.financeservice.weight_range;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.financeservice.FinanceURL;
import com.ftcs.financeservice.weight_range.dto.WeightRangeRequestDTO;
import com.ftcs.financeservice.weight_range.model.WeightRange;
import com.ftcs.financeservice.weight_range.service.WeightRangeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(FinanceURL.WEIGHT_RANGE)
public class WeightRangeController {

    private final WeightRangeService weightRangeService;


    @GetMapping
    @PreAuthorize("hasPermission(null, 'FINANCE') or hasPermission(null, 'ADMIN')")
    public ApiResponse<?> getAllWeightRages(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                            @RequestParam(value = "size", defaultValue = "10") Integer size) {
        Page<WeightRange> weightRanges = weightRangeService.getAllWeightRanges(page, size);
        return new ApiResponse<>("Fetched weight rages surcharges successfully.", weightRanges);
    }

    @GetMapping("/{weightRageId}")
    @PreAuthorize("hasPermission(null, 'FINANCE') or hasPermission(null, 'ADMIN')")
    public ApiResponse<?> getWeightRangeById(@PathVariable("weightRageId") Integer weightRageId) {
        return new ApiResponse<>(weightRangeService.findByWeightRangeId(weightRageId));
    }

    @PostMapping
    @PreAuthorize("hasPermission(null, 'FINANCE') or hasPermission(null, 'ADMIN')")
    public ApiResponse<?> createWeightRange(@Valid @RequestBody WeightRangeRequestDTO requestDTO,
                                              @RequestAttribute("accountId") Integer accountId) {
        weightRangeService.createWeightRange(accountId, requestDTO);
        return new ApiResponse<>("Created weight range successfully.");
    }
}