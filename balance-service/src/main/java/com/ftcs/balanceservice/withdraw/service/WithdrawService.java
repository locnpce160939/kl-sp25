package com.ftcs.balanceservice.withdraw.service;

import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.transportation.trip_booking.repository.TripBookingsRepository;
import com.ftcs.balanceservice.withdraw.constant.WithdrawStatus;
import com.ftcs.balanceservice.withdraw.dto.WithdrawRequestDTO;
import com.ftcs.balanceservice.withdraw.model.Withdraw;
import com.ftcs.balanceservice.withdraw.repository.WithdrawRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WithdrawService {
    private final WithdrawRepository withdrawRepository;
    private final AccountRepository accountRepository;

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
    }

    public void updateStatusWithdraw(WithdrawRequestDTO requestDTO, Long withdrawId) {
        Withdraw withdraw = findWithdrawById(withdrawId);
        if (requestDTO.getStatus() == WithdrawStatus.APPROVED) {
            Account account = findAccountByAccountId(withdraw.getAccountId());
            account.setBalance(account.getBalance() - withdraw.getAmount());
            accountRepository.save(account);
        }
        withdraw.setStatus(requestDTO.getStatus());
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

    public List<Withdraw> getAll(){
        return withdrawRepository.findAll();
    }

    public List<Withdraw> getAllByAccountId(Integer accountId) {
        return withdrawRepository.findAllByAccountId(accountId);
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

    private Account findAccountByAccountId(Integer accountId) {
        return accountRepository.findAccountByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("Account not found"));
    }

    public Withdraw findWithdrawById(Long withdrawId) {
        return withdrawRepository.findByWithdrawId(withdrawId)
                .orElseThrow(() -> new BadRequestException("Withdraw not found"));
    }
}
