package com.ftcs.voucherservice;

import com.ftcs.common.dto.ApiResponse;
import com.ftcs.voucherservice.constant.UserType;
import com.ftcs.voucherservice.constant.VoucherStatus;
import com.ftcs.voucherservice.dto.UpdateStatusVoucherRequestDTO;
import com.ftcs.voucherservice.dto.VoucherRequestDTO;
import com.ftcs.voucherservice.dto.VoucherValidationDTO;
import com.ftcs.voucherservice.model.Voucher;
import com.ftcs.voucherservice.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.units.qual.A;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(VoucherURL.VOUCHER)
public class VoucherController {
    private final VoucherService voucherService;

    @PostMapping
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<?> createVoucher(@Valid @RequestBody VoucherRequestDTO requestDTO) {
        voucherService.createVoucher(requestDTO);
        return new ApiResponse<>("Created voucher successfully");
    }

    @PutMapping("/{voucherId}")
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<?> updateVoucher(@Valid @RequestBody VoucherRequestDTO requestDTO,
                                        @PathVariable("voucherId") Long voucherId) {
        voucherService.updateVoucher(requestDTO, voucherId);
        return new ApiResponse<>("Updated voucher successfully");
    }

    @PutMapping("/status/{voucherId}")
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<?> updateByStatus(@Valid @RequestBody UpdateStatusVoucherRequestDTO requestDTO,
                                         @PathVariable("voucherId") Long voucherId) {
        voucherService.updateVoucherStatus(voucherId, requestDTO);
        return new ApiResponse<>("Updated status voucher successfully");
    }

    @GetMapping("/{voucherId}")
    public ApiResponse<Voucher> findVoucher(@PathVariable("voucherId") Long voucherId) {
        return new ApiResponse<>(voucherService.findVoucherById(voucherId));
    }

    @GetMapping("/code/{voucherCode}")
    public ApiResponse<Voucher> getVoucherByCode(@PathVariable("voucherCode") String voucherCode) {
        return new ApiResponse<>(voucherService.findVoucherByCode(voucherCode));
    }

    @GetMapping("/status/{status}")
    public ApiResponse<Page<Voucher>> getVouchersByStatus(@PathVariable("status") VoucherStatus status,
                                                          @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                          @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(voucherService.findAllByStatusManagement(status, page, size));
    }

    @GetMapping
    public ApiResponse<Page<Voucher>> getAllVouchers(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                     @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(voucherService.findAll(page, size));
    }

    @PostMapping("/applicable")
    public ApiResponse<List<Voucher>> getApplicableVouchers(
            @RequestAttribute("accountId") Integer accountId,
            @Valid @RequestBody VoucherValidationDTO requestDTO) {
        return new ApiResponse<>(voucherService.getApplicableVouchersForUser(accountId, requestDTO));
    }

    @PutMapping("/delete/{voucherId}")
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<?> deleteVoucher(@PathVariable("voucherId") Long voucherId) {
        voucherService.deleteVoucher(voucherId);
        return new ApiResponse<>("Deleted voucher successfully");
    }

    @GetMapping("/list")
    public ApiResponse<Page<Voucher>> getAllVouchersActive(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                           @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(voucherService.findAllActiveVouchers(page, size));
    }

    @GetMapping("/redeem")
    public ApiResponse<Page<Voucher>> getAllVoucherRedeem(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                          @RequestParam(value = "size", defaultValue = "10") Integer size,
                                                          @RequestParam(value = "isRedeemable", required = false) Boolean isRedeemable){
        return new ApiResponse<>(voucherService.getAllVouchers(isRedeemable, page, size));
    }

    @GetMapping("/canRedemption")
    public ApiResponse<List<Voucher>> getCanRedemptionVouchers(@RequestAttribute("accountId") Integer accountId,
                                                               @RequestAttribute("role") UserType userType) {
        return new ApiResponse<>(voucherService.getVouchersAvailableForRedemption(accountId, userType));
    }

    @PutMapping("/redeem/{voucherId}")
    public ApiResponse<Voucher> redeemVoucherWithPoints(
            @RequestAttribute("accountId") Integer accountId,
            @PathVariable("voucherId") Long voucherId) {
        return new ApiResponse<>(voucherService.redeemVoucherWithPoints(accountId, voucherId));
    }

}
