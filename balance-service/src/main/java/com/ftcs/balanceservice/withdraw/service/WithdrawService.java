package com.ftcs.balanceservice.withdraw.service;

import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.balanceservice.balance_history.service.BalanceHistoryService;
import com.ftcs.balanceservice.withdraw.dto.WithdrawExportDTO;
import com.ftcs.balanceservice.withdraw.dto.WithdrawTotalExportDTO;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.balanceservice.withdraw.constant.WithdrawStatus;
import com.ftcs.balanceservice.withdraw.dto.WithdrawRequestDTO;
import com.ftcs.balanceservice.withdraw.model.Withdraw;
import com.ftcs.balanceservice.withdraw.repository.WithdrawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WithdrawService {
    private final WithdrawRepository withdrawRepository;
    private final AccountRepository accountRepository;
    private final BalanceHistoryService balanceHistoryService;

    public void createWithdraw(WithdrawRequestDTO requestDTO, Integer accountId) {
        Account account = findAccountByAccountId(accountId);
        validateAmount(account, requestDTO);
        Withdraw withdraw = new Withdraw();
        withdraw.setAmount(requestDTO.getAmount());
        withdraw.setAccountId(accountId);
        withdraw.setBankName(requestDTO.getBankName());
        withdraw.setBankAccountNumber(requestDTO.getBankAccountNumber());
        withdraw.setStatus(WithdrawStatus.PENDING);
        withdraw.setRequestDate(LocalDateTime.now());
        withdrawRepository.save(withdraw);

        balanceHistoryService.recordWithdrawalRequest(
                withdraw.getWithdrawId(),
                accountId,
                requestDTO.getAmount());
        account.setBalance(account.getBalance() - requestDTO.getAmount());
        accountRepository.save(account);
    }

//    public void updateStatusWithdraw(WithdrawRequestDTO requestDTO, Long withdrawId) {
//        Withdraw withdraw = findWithdrawById(withdrawId);
//        if (requestDTO.getStatus() == WithdrawStatus.APPROVED) {
//            Account account = findAccountByAccountId(withdraw.getAccountId());
//            account.setBalance(account.getBalance() - withdraw.getAmount());
//            accountRepository.save(account);
//        }
//        withdraw.setStatus(requestDTO.getStatus());
//        withdrawRepository.save(withdraw);
//    }

    public void updateStatusWithdraw(WithdrawRequestDTO requestDTO, Long withdrawId) {
        Withdraw withdraw = findWithdrawById(withdrawId);

        // Prevent status change if already processed
        if (withdraw.getStatus() != WithdrawStatus.PENDING) {
            throw new BadRequestException("Cannot update withdrawal that is already " + withdraw.getStatus());
        }

        if (requestDTO.getStatus() == WithdrawStatus.REJECTED) {
            // Refund the money if rejected
            Account account = findAccountByAccountId(withdraw.getAccountId());
            account.setBalance(account.getBalance() + withdraw.getAmount());
            accountRepository.save(account);

            // Record balance history for rejected withdrawal (refund)
            balanceHistoryService.recordWithdrawalRejected(
                    withdraw.getWithdrawId(),
                    withdraw.getAccountId(),
                    withdraw.getAmount());
        } else if (requestDTO.getStatus() == WithdrawStatus.APPROVED) {
            // Record balance history for approved withdrawal (no balance change as it was already deducted)
            balanceHistoryService.recordWithdrawalApproved(
                    withdraw.getWithdrawId(),
                    withdraw.getAccountId());
        }

        withdraw.setStatus(requestDTO.getStatus());
        withdraw.setProcessedDate(LocalDateTime.now()); // Set processed date
        withdrawRepository.save(withdraw);
    }


    public void updateForDriver(WithdrawRequestDTO requestDTO, Long withdrawId, Integer accountId) {
        Account account = findAccountByAccountId(accountId);
        Withdraw withdraw = findWithdrawById(withdrawId);
        validateAmount(account, requestDTO);
        withdraw.setAmount(requestDTO.getAmount());
        withdraw.setBankName(requestDTO.getBankName());
        withdraw.setBankAccountNumber(requestDTO.getBankAccountNumber());
        withdraw.setStatus(WithdrawStatus.PENDING);
        withdraw.setRequestDate(LocalDateTime.now());
        withdrawRepository.save(withdraw);
    }

    public void deleteWithdraw(Long withdrawId) {
        withdrawRepository.deleteById(withdrawId);
    }

    public Page<Withdraw> getAll(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page, size);
        return withdrawRepository.findAll(pageable);
    }

    public List<Withdraw> getAllByAccountId(Integer accountId) {
        return withdrawRepository.findAllByAccountId(accountId);
    }

    public int batchUpdateWithdrawStatus(List<Long> withdrawIds, WithdrawStatus status) {
        if (withdrawIds == null || withdrawIds.isEmpty()) {
            throw new BadRequestException("No withdrawal IDs provided");
        }

        int updatedCount = 0;

        for (Long withdrawId : withdrawIds) {
            try {
                Withdraw withdraw = findWithdrawById(withdrawId);

                // Skip already processed withdrawals
                if (withdraw.getStatus() != WithdrawStatus.PENDING) {
                    continue;
                }

                // Handle balance changes based on new status
                if (status == WithdrawStatus.REJECTED) {
                    // Refund the money if rejected
                    Account account = findAccountByAccountId(withdraw.getAccountId());
                    account.setBalance(account.getBalance() + withdraw.getAmount());
                    accountRepository.save(account);

                    // Record balance history for rejected withdrawal (refund)
                    balanceHistoryService.recordWithdrawalRejected(
                            withdraw.getWithdrawId(),
                            withdraw.getAccountId(),
                            withdraw.getAmount());
                } else if (status == WithdrawStatus.APPROVED) {
                    // Record balance history for approved withdrawal
                    balanceHistoryService.recordWithdrawalApproved(
                            withdraw.getWithdrawId(),
                            withdraw.getAccountId());
                }

                // Update withdraw status and processed date
                withdraw.setStatus(status);
                withdraw.setProcessedDate(LocalDateTime.now());
                withdrawRepository.save(withdraw);

                updatedCount++;
            } catch (Exception e) {
                // Log error but continue processing other withdrawals
                System.err.println("Failed to update withdraw ID " + withdrawId + ": " + e.getMessage());
            }
        }

        if (updatedCount == 0) {
            throw new BadRequestException("No withdrawals were updated. Check that the selected withdrawals have PENDING status.");
        }

        return updatedCount;
    }

    public List<Withdraw> getAllByAccountIdManagement(Integer accountId) {
        return withdrawRepository.findAllByAccountId(accountId);
    }

    private void validateAmount(Account account, WithdrawRequestDTO requestDTO) {
        if(account.getBalance() < requestDTO.getAmount()) {
            throw new BadRequestException("Not enough balance");
        }else if(requestDTO.getAmount() < 100000.00) {
            throw new BadRequestException("You must withdraw more than 100000");
        }
    }

    public WithdrawExportDTO exportWithdraw(Long withdrawId) {
        Withdraw withdraw = findWithdrawById(withdrawId);
        Account account = findAccountByAccountId(withdraw.getAccountId());

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        WithdrawExportDTO exportDTO = new WithdrawExportDTO();
        exportDTO.setWithdrawId(withdraw.getWithdrawId());
        exportDTO.setAccountId(withdraw.getAccountId());
        exportDTO.setUsername(account.getUsername());
        exportDTO.setAmount(withdraw.getAmount());
        exportDTO.setBankName(withdraw.getBankName());
        exportDTO.setBankAccountNumber(withdraw.getBankAccountNumber());
        exportDTO.setStatus(withdraw.getStatus());
        exportDTO.setRequestDate(withdraw.getRequestDate().format(formatter));

        if (withdraw.getProcessedDate() != null) {
            exportDTO.setProcessedDate(withdraw.getProcessedDate().format(formatter));
        }

        return exportDTO;
    }

    public WithdrawTotalExportDTO exportTotalWithdrawByAccountId(Integer accountId) {
        Account account = findAccountByAccountId(accountId);
        List<Withdraw> withdrawals = getAllByAccountId(accountId);

        double totalAmount = 0.0;
        int totalApproved = 0;
        int totalRejected = 0;
        int totalPending = 0;

        for (Withdraw withdraw : withdrawals) {
            if (withdraw.getStatus() == WithdrawStatus.APPROVED) {
                totalAmount += withdraw.getAmount();
                totalApproved++;
            } else if (withdraw.getStatus() == WithdrawStatus.REJECTED) {
                totalRejected++;
            } else if (withdraw.getStatus() == WithdrawStatus.PENDING) {
                totalPending++;
            }
        }

        WithdrawTotalExportDTO exportDTO = new WithdrawTotalExportDTO();
        exportDTO.setAccountId(accountId);
        exportDTO.setUsername(account.getUsername());
        exportDTO.setCurrentBalance(account.getBalance());
        exportDTO.setTotalWithdrawAmount(totalAmount);
        exportDTO.setTotalWithdrawals(withdrawals.size());
        exportDTO.setTotalApproved(totalApproved);
        exportDTO.setTotalRejected(totalRejected);
        exportDTO.setTotalPending(totalPending);
        exportDTO.setExportDate(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

        return exportDTO;
    }

    public List<Withdraw> getAllByStatus(WithdrawStatus status) {
        return withdrawRepository.findAllByStatus(status);
    }

    public Page<Withdraw> getAllByStatusManagement(WithdrawStatus status, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return withdrawRepository.findAllByStatus(status, pageable);
    }

    public List<Withdraw> getAllByRequestDate(LocalDate requestDate) {
        LocalDateTime startOfDay = requestDate.atStartOfDay();  // 2025-02-19T00:00:00
        LocalDateTime endOfDay = requestDate.atTime(23, 59, 59);  // 2025-02-19T23:59:59

        return withdrawRepository.findAllByRequestDateBetween(startOfDay, endOfDay);
    }

    public Page<Withdraw> getAllByRequestDateManagement(LocalDate requestDate, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        LocalDateTime startOfDay = requestDate.atStartOfDay();  // 2025-02-19T00:00:00
        LocalDateTime endOfDay = requestDate.atTime(23, 59, 59);  // 2025-02-19T23:59:59

        return withdrawRepository.findAllByRequestDateBetween(startOfDay, endOfDay, pageable);
    }

    private Account findAccountByAccountId(Integer accountId) {
        return accountRepository.findAccountByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("Account not found"));
    }

    public Withdraw findWithdrawById(Long withdrawId) {
        return withdrawRepository.findByWithdrawId(withdrawId)
                .orElseThrow(() -> new BadRequestException("Withdraw not found"));
    }
}
