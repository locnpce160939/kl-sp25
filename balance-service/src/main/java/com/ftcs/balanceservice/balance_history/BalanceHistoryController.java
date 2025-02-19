package com.ftcs.balanceservice.balance_history;

import com.ftcs.balanceservice.BalanceURL;
import com.ftcs.balanceservice.balance_history.model.BalanceHistory;
import com.ftcs.balanceservice.balance_history.service.BalanceHistoryService;
import com.ftcs.balanceservice.withdraw.model.Withdraw;
import com.ftcs.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(BalanceURL.BALANCE_HISTORY)
public class BalanceHistoryController {
    private final BalanceHistoryService balanceHistoryService;

    @GetMapping()
    @PreAuthorize("hasPermission(null, 'ADMIN')")
    public ApiResponse<List<BalanceHistory>> getAllBalanceHistory() {
        return new ApiResponse<>(balanceHistoryService.findAll());
    }

    @GetMapping("/account")
    public ApiResponse<List<BalanceHistory>> getAllBalanceHistoryByAccountId(@RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(balanceHistoryService.findAllByAccountId(accountId));
    }

    @GetMapping("/{balanceHistoryId}")
    public ApiResponse<BalanceHistory> getAllBalanceHistoryByid(@PathVariable("balanceHistoryId") Long balanceHistoryId) {
        return new ApiResponse<>(balanceHistoryService.findById(balanceHistoryId));
    }


}
