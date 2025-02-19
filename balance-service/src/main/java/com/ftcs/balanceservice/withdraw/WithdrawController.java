package com.ftcs.balanceservice.withdraw;

import com.ftcs.balanceservice.BalanceURL;
import com.ftcs.common.dto.ApiResponse;
import com.ftcs.balanceservice.withdraw.dto.WithdrawRequestDTO;
import com.ftcs.balanceservice.withdraw.model.Withdraw;
import com.ftcs.balanceservice.withdraw.service.WithdrawService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(BalanceURL.WITH_DRAW)
public class WithdrawController {
    private final WithdrawService withdrawService;

    @GetMapping()
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<List<Withdraw>> getAllWithdraws() {
        return new ApiResponse<>(withdrawService.getAll());
    }

    @GetMapping("/{withdrawId}")
    public ApiResponse<Withdraw> getWithdrawById(@PathVariable("withdrawId") Long withdrawId) {
        return new ApiResponse<>(withdrawService.findWithdrawById(withdrawId));
    }

    @PostMapping()
    public ApiResponse<String> createWithdraw(@RequestAttribute("accountId") Integer accountId,
                                              @RequestBody @Valid WithdrawRequestDTO requestDTO) {
        withdrawService.createWithdraw(requestDTO, accountId);
        return new ApiResponse<>("Withdraw request created successfully");
    }

    @PutMapping("/{withdrawId}")
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<String> updateStatusWithdraw(@PathVariable("withdrawId") Long withdrawId,
                                                    @RequestBody @Valid WithdrawRequestDTO requestDTO) {
        withdrawService.updateStatusWithdraw(requestDTO, withdrawId);
        return new ApiResponse<>("Withdraw status updated successfully");
    }

    @DeleteMapping("/{withdrawId}")
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<String> deleteWithdraw(@PathVariable("withdrawId") Long withdrawId) {
        withdrawService.deleteWithdraw(withdrawId);
        return new ApiResponse<>("Withdraw request deleted successfully");
    }

    @GetMapping("/account")
    public ApiResponse<List<Withdraw>> getAllWithdrawsByAccount(@RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(withdrawService.getAllByAccountId(accountId));
    }

    @GetMapping("/management/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<List<Withdraw>> getAllWithdrawsManagement(@PathVariable("accountId") Integer accountId) {
        return new ApiResponse<>(withdrawService.getAllByAccountIdManagement(accountId));
    }

    @PutMapping("/driver/{withdrawId}")
    public ApiResponse<?> updateForDriver(@PathVariable("withdrawId") Long withdrawId,
                                          @RequestBody @Valid WithdrawRequestDTO requestDTO,
                                          @RequestAttribute("accountId") Integer accountId) {
        withdrawService.updateForDriver(requestDTO, withdrawId, accountId);
        return new ApiResponse<>("Withdraw request updated successfully");
    }
}

