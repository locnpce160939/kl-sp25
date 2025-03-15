package com.ftcs.financeservice.pricing;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.financeservice.FinanceURL;
import com.ftcs.financeservice.pricing.dto.PricingRequestDTO;
import com.ftcs.financeservice.pricing.model.Pricing;
import com.ftcs.financeservice.pricing.service.PricingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(FinanceURL.PRICING)
public class PricingController {

    private final PricingService pricingService;

    @GetMapping
    @PreAuthorize("hasPermission(null, 'FINANCE') or hasPermission(null, 'ADMIN')")
    public ApiResponse<?> getAll(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                 @RequestParam(value = "size", defaultValue = "10") Integer size){
        return new ApiResponse<>("Get All Pricing", pricingService.getAllPricing(page, size));
    }

    @GetMapping("/{pricingId}")
    @PreAuthorize("hasPermission(null, 'FINANCE') or hasPermission(null, 'ADMIN')")
    public ApiResponse<?> getPricingById(@PathVariable("pricingId") Integer pricingId){
        return new ApiResponse<>(pricingService.getPricingById(pricingId));
    }

    @PostMapping
    @PreAuthorize("hasPermission(null, 'FINANCE') or hasPermission(null, 'ADMIN')")
    public ApiResponse<?> addPricing(@Valid @RequestBody PricingRequestDTO requestDTO,
                                     @RequestAttribute("accountId") Integer accountId){
        pricingService.createPricing(accountId, requestDTO);
        return new ApiResponse<>("Create Pricing Successfully!");
    }

    @PutMapping("/{pricingId}")
    @PreAuthorize("hasPermission(null, 'FINANCE') or hasPermission(null, 'ADMIN')")
    public ApiResponse<?> updatePricing(@Valid @RequestBody PricingRequestDTO requestDTO,
                                        @PathVariable("pricingId") Integer pricingId,
                                        @RequestAttribute("accountId") Integer accountId){
        pricingService.updatePricing(accountId, pricingId, requestDTO);
        return new ApiResponse<>("Update Pricing Successfully!");
    }

    @DeleteMapping("/{pricingId}")
    @PreAuthorize("hasPermission(null, 'FINANCE') or hasPermission(null, 'ADMIN')")
    public ApiResponse<?> deletePricing(@PathVariable("pricingId") Integer pricingId){
        pricingService.deletePricing(pricingId);
        return new ApiResponse<>("Delete Pricing Successfully!");
    }
}
