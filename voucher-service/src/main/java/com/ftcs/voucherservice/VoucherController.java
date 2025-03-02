package com.ftcs.voucherservice;

import com.ftcs.balanceservice.BalanceURL;
import com.ftcs.balanceservice.payment.model.Payment;
import com.ftcs.common.dto.ApiResponse;
import com.ftcs.voucherservice.constant.VoucherStatus;
import com.ftcs.voucherservice.dto.UpdateStatusVoucherRequestDTO;
import com.ftcs.voucherservice.dto.VoucherRequestDTO;
import com.ftcs.voucherservice.dto.VoucherValidationDTO;
import com.ftcs.voucherservice.model.Voucher;
import com.ftcs.voucherservice.service.VoucherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ApiResponse<List<Voucher>> getVouchersByStatus(@PathVariable("status") VoucherStatus status) {
        return new ApiResponse<>(voucherService.findAllByStatus(status));
    }

    @GetMapping
    public ApiResponse<List<Voucher>> getAllVouchers() {
        return new ApiResponse<>(voucherService.findAll());
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
}
