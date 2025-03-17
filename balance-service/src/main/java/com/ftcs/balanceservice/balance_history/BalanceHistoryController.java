package com.ftcs.balanceservice.balance_history;

import com.ftcs.balanceservice.BalanceURL;
import com.ftcs.balanceservice.balance_history.model.BalanceHistory;
import com.ftcs.balanceservice.balance_history.service.BalanceHistoryService;
import com.ftcs.balanceservice.withdraw.model.Withdraw;
import com.ftcs.common.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
    public ApiResponse<Page<BalanceHistory>> getAllBalanceHistory(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                  @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(balanceHistoryService.findAll(page, size));
    }

    @GetMapping("/account/management")
    public ApiResponse<Page<BalanceHistory>> getAllBalanceHistoryByAccountId(@RequestAttribute("accountId") Integer accountId,
                                                                             @RequestParam(value = "page", defaultValue = "0") Integer page,
                                                                             @RequestParam(value = "size", defaultValue = "10") Integer size) {
        return new ApiResponse<>(balanceHistoryService.findAllByAccountIdManagement(accountId, page, size));
    }

    @GetMapping("/account")
    public ApiResponse<List<BalanceHistory>> getAllBalanceHistoryByAccountIdDriver(@RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(balanceHistoryService.findAllByAccountId(accountId));
    }

    @GetMapping("/{balanceHistoryId}")
    public ApiResponse<BalanceHistory> getAllBalanceHistoryById(@PathVariable("balanceHistoryId") Long balanceHistoryId) {
        return new ApiResponse<>(balanceHistoryService.findById(balanceHistoryId));
    }


}
