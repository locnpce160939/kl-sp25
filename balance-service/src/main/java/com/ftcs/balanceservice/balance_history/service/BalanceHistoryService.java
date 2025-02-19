package com.ftcs.balanceservice.balance_history.service;

import com.ftcs.balanceservice.balance_history.constant.TransactionType;
import com.ftcs.balanceservice.balance_history.model.BalanceHistory;
import com.ftcs.balanceservice.balance_history.repository.BalanceHistoryRepository;
import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.common.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BalanceHistoryService {
    private final BalanceHistoryRepository balanceHistoryRepository;
    private final AccountRepository accountRepository;

    public void recordPaymentCredit(Long paymentId, Integer accountId, Double amount) {
        Account account = findAccountById(accountId);
        Double previousBalance = account.getBalance() + amount;
        Double currentBalance = account.getBalance();

        BalanceHistory history = BalanceHistory.builder()
                .accountId(accountId)
                .amount(amount)
                .transactionType(TransactionType.PAYMENT_RECEIVED)
                .referenceId(paymentId)
                .previousBalance(previousBalance)
                .currentBalance(currentBalance)
                .description("Payment received for booking")
                .build();

        balanceHistoryRepository.save(history);
        log.info("Created balance history record for payment {}: {} -> {}",
                paymentId, previousBalance, currentBalance);
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
                .amount(0.0) // Zero amount since balance was already deducted at request time
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

    public List<BalanceHistory> findAllByAccountId(Integer accountId) {
        return balanceHistoryRepository.findByAccountId(accountId);
    }

    public List<BalanceHistory> findAll(){
        return balanceHistoryRepository.findAll();
    }

    public BalanceHistory findById(Long balanceHistoryId) {
        return balanceHistoryRepository.findById(balanceHistoryId).orElse(null);
    }
}