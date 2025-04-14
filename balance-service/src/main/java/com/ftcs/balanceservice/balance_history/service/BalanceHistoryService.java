package com.ftcs.balanceservice.balance_history.service;

import com.ftcs.balanceservice.balance_history.constant.TransactionType;
import com.ftcs.balanceservice.balance_history.model.BalanceHistory;
import com.ftcs.balanceservice.balance_history.repository.BalanceHistoryRepository;
import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.common.exception.BadRequestException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceHistoryService {
    private final BalanceHistoryRepository balanceHistoryRepository;
    private final AccountRepository accountRepository;

    @Transactional
    public void recordPaymentCredit(Long bookingId, Integer accountId, Double amount) {
        log.info("Creating balance history for trip completion: bookingId={}, accountId={}, amount={}",
                bookingId, accountId, amount);

        Account account = accountRepository.findAccountByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("Account not found: " + accountId));

        BalanceHistory history = BalanceHistory.builder()
                .accountId(accountId)
                .amount(amount)
                .transactionType(TransactionType.PAYMENT_RECEIVED) // Thêm type này vào enum TransactionType
                .referenceId(bookingId)
                .previousBalance(account.getBalance() - amount)
                .currentBalance(account.getBalance())
                .description("Payment received for completed trip #" + bookingId)
                .build();

        balanceHistoryRepository.save(history);
        log.info("Successfully created balance history: {}", history);
    }

    // Add this new method to the BalanceHistoryService class
    @Transactional
    public void recordBonus(Long bonusProgressId, Integer accountId, Double amount) {
        log.info("Creating balance history for driver bonus: progressId={}, accountId={}, amount={}",
                bonusProgressId, accountId, amount);

        Account account = findAccountById(accountId);
        Double previousBalance = account.getBalance() - amount;

        BalanceHistory history = BalanceHistory.builder()
                .accountId(accountId)
                .amount(amount)
                .transactionType(TransactionType.BONUS_RECEIVED) // Add this new type to your TransactionType enum
                .referenceId(bonusProgressId)
                .previousBalance(previousBalance)
                .currentBalance(account.getBalance())
                .description("Bonus payment received for completed bonus challenge #" + bonusProgressId)
                .build();

        balanceHistoryRepository.save(history);
        log.info("Successfully created balance history for bonus payment: {}", history);
    }

    public void recordWithdrawalRequest(Long withdrawId, Integer accountId, Double amount) {
        Account account = findAccountById(accountId);
        Double currentBalance = account.getBalance();
        Double previousBalance = currentBalance - amount;

        BalanceHistory history = BalanceHistory.builder()
                .accountId(accountId)
                .amount(-amount)
                .transactionType(TransactionType.WITHDRAW_REQUESTED)
                .referenceId(withdrawId)
                .previousBalance(previousBalance)
                .currentBalance(currentBalance)
                .description("Withdrawal requested and balance reserved")
                .build();

        balanceHistoryRepository.save(history);
        log.info("Created balance history record for withdrawal request {}: {} -> {}",
                withdrawId, previousBalance, currentBalance);
    }

    public void recordWithdrawalApproved(Long withdrawId, Integer accountId) {
        Account account = findAccountById(accountId);

        BalanceHistory history = BalanceHistory.builder()
                .accountId(accountId)
                .amount(0.0)
                .transactionType(TransactionType.WITHDRAW_APPROVED)
                .referenceId(withdrawId)
                .previousBalance(account.getBalance())
                .currentBalance(account.getBalance())
                .description("Withdrawal request approved and processed")
                .build();

        balanceHistoryRepository.save(history);
        log.info("Created balance history record for approved withdrawal {}", withdrawId);
    }

    public void recordWithdrawalRejected(Long withdrawId, Integer accountId, Double amount) {
        Account account = findAccountById(accountId);
        Double previousBalance = account.getBalance() + amount; // Balance before refund
        Double currentBalance = account.getBalance(); // Balance after refund

        BalanceHistory history = BalanceHistory.builder()
                .accountId(accountId)
                .amount(amount) // Positive amount for refund
                .transactionType(TransactionType.WITHDRAW_REJECTED)
                .referenceId(withdrawId)
                .previousBalance(previousBalance)
                .currentBalance(currentBalance)
                .description("Withdrawal rejected and funds returned to balance")
                .build();

        balanceHistoryRepository.save(history);
        log.info("Created balance history record for rejected withdrawal {}: {} -> {}",
                withdrawId, previousBalance, currentBalance);
    }

    private Account findAccountById(Integer accountId) {
        return accountRepository.findAccountByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("Account not found with ID: " + accountId));
    }

    public Page<BalanceHistory> findAllByAccountIdManagement(Integer accountId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return balanceHistoryRepository.findByAccountId(accountId, pageable);
    }

    public List<BalanceHistory> findAllByAccountId(Integer accountId) {
        return balanceHistoryRepository.findByAccountId(accountId);
    }

    public Page<BalanceHistory> findAll(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        return balanceHistoryRepository.findAll(pageable);
    }

    public BalanceHistory findById(Long balanceHistoryId) {
        return balanceHistoryRepository.findById(balanceHistoryId).orElse(null);
    }
}