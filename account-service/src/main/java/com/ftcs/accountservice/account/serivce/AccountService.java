package com.ftcs.accountservice.account.serivce;

import com.ftcs.accountservice.account.dto.*;
import com.ftcs.accountservice.account.dto.register.RegisterConfirmRequestDTO;
import com.ftcs.accountservice.account.dto.register.RegisterRequestDTO;
import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.authservice.features.account.contacts.Rank;
import com.ftcs.authservice.features.account.contacts.RoleType;
import com.ftcs.authservice.features.account.contacts.StatusAccount;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.common.exception.NotFoundException;
import com.ftcs.common.service.SendMailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final SendMailService sendMailService;
    private final PasswordEncoder passwordEncoder;

    public void registerSendUser(RegisterRequestDTO requestDTO, HttpServletRequest request) {
        isExistingAccount(requestDTO);
        int randomNumber = generateOtpCode();
        String subject = "OTP authentication";
        sendMailService.sendOtp(requestDTO.getEmail(), subject, randomNumber);
        request.getSession().setAttribute("code_register", String.valueOf(randomNumber));
    }

    public Account registerConfirmUser(RegisterConfirmRequestDTO requestDTO, HttpServletRequest request) {
        String sessionOtp = (String) request.getSession().getAttribute("code_register");
        if (!requestDTO.getOtp().equals(sessionOtp)) {
            throw new BadRequestException("Invalid OTP!");
        }

        Account account = new Account();
        account.setUsername(requestDTO.getUsername());
        account.setEmail(requestDTO.getEmail());
        account.setFullName(requestDTO.getFullName());
        account.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        account.setPhone(requestDTO.getPhone());
        if(requestDTO.getRole() == RoleType.DRIVER){
            account.setStatus(StatusAccount.PENDING);
        }else if(requestDTO.getRole() == RoleType.CUSTOMER){
            account.setStatus(StatusAccount.ACTIVE);
        }

        account.setRole(requestDTO.getRole());
        account.setBalance(0.00);
        account.setRedeemablePoints(0);
        account.setLoyaltyPoints(0);
        account.setRanking(Rank.BRONZE);
        return accountRepository.save(account);
    }

    public Account createNewAccount(RegisterRequestDTO requestDTO) {
        isExistingAccount(requestDTO);

        Account account = new Account();
        account.setUsername(requestDTO.getUsername());
        account.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        account.setPhone(requestDTO.getPhone());
        account.setFullName(requestDTO.getFullName());
        account.setRole(requestDTO.getRole());
        account.setEmail(requestDTO.getEmail());
        account.setStatus(StatusAccount.ACTIVE);
        account.setBalance(0.00);
        return accountRepository.save(account);
    }

    public Account updateAccount(Integer accountId, RegisterRequestDTO requestDTO) {
        Account account = findAccountByAccountId(accountId);

        if (accountRepository.existsByUsernameAndAccountIdNot(requestDTO.getUsername(), accountId)) {
            throw new BadRequestException("Username Exists!");
        }
        if (accountRepository.existsByEmailAndAccountIdNot(requestDTO.getEmail(), accountId)) {
            throw new BadRequestException("Email Exists!");
        }

        account.setUsername(requestDTO.getUsername());
        account.setPhone(requestDTO.getPhone());
        account.setFullName(requestDTO.getFullName());
        account.setEmail(requestDTO.getEmail());

        return accountRepository.save(account);
    }


    public void deleteAccount(Integer id) {
        Account account = findAccountByAccountId(id);
        account.setStatus(StatusAccount.IS_DISABLED);
        accountRepository.save(account);
    }

    public Account profile(Integer id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Account does not exist!"));
        account.setPassword("");
        return account;
    }

    public Account updateProfile(UpdateProfileRequestDTO requestDTO, Integer accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BadRequestException("Account does not exist!"));
        account.setPhone(requestDTO.getPhone());
        account.setFullName(requestDTO.getFullName());
        accountRepository.save(account);
        return account;
    }

    public Page<Account> getAllAccounts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Account> accounts = accountRepository.findAll(pageable);
        accounts.forEach(account -> account.setPassword(""));
        return accounts;
    }

    public Account getAccountById(Integer id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Account does not exist!"));
    }

    public void forgotPasswordAccountSend(ForgotPasswordAccountRequestDTO requestDTO, HttpServletRequest request) {
        if (!accountRepository.existsByEmail(requestDTO.getEmail())) {
            throw new BadRequestException("Email does not exist!");
        }
        int otpCode = generateOtpCode();
        String subject = "OTP Authentication for Password Reset";
        sendMailService.sendOtp(requestDTO.getEmail(), subject, otpCode);
        request.getSession().setAttribute("code_forgot", String.valueOf(otpCode));
    }

    public void forgotPasswordAccountConfirm(ForgotPasswordAccountRequestDTO requestDTO, HttpServletRequest request) {
        String sessionOtp = (String) request.getSession().getAttribute("code_forgot");
        if (!requestDTO.getOtp().equals(sessionOtp)) {
            throw new BadRequestException("Invalid OTP!");
        }
        if (!requestDTO.getNewPassword().equals(requestDTO.getConfirmPassword())) {
            throw new BadRequestException("RePassword not match!");
        }
        Account account = accountRepository.findAccountByEmail(requestDTO.getEmail());
        if (account == null) {
            throw new NotFoundException("Account not found!");
        } else if (account.getRole() == null) {
            throw new BadRequestException("Invalid role!");
        }
        account.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        accountRepository.save(account);
    }

    public void changePasswordAccount(ChangePasswordRequestDTO request, Integer accountId) {
        Account account = findAccountByAccountId(accountId);
        if (account == null) {
            throw new NotFoundException("Account not found!");
        }
        if (!passwordEncoder.matches(request.getOldPassword(), account.getPassword())) {
            throw new BadRequestException("Old password is not correct!");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BadRequestException("RePassword not match!");
        }
        account.setPassword(passwordEncoder.encode(request.getNewPassword()));
        accountRepository.save(account);
    }

    public Page<Account> findAllByRole(RoleType role, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Account> accounts = accountRepository.findAllByRoleAndStatusNot(role, StatusAccount.IS_DISABLED, pageable);
        if (accounts.isEmpty()) {
            throw new NotFoundException("No accounts found for the role: " + role);
        }
        accounts.forEach(account -> account.setPassword(""));
        return accounts;
    }

    private int generateOtpCode() {
        return new Random().nextInt(900000) + 100000;
    }

    private Account findAccountByAccountId(Integer accountId) {
        return accountRepository.findAccountByAccountId(accountId)
                .orElseThrow(() -> new BadRequestException("Account does not exist!"));
    }

    private void isExistingAccount(RegisterRequestDTO requestDTO) {
        if (accountRepository.existsByUsername(requestDTO.getUsername())) {
            throw new BadRequestException("Username Exists!");
        }
        if (accountRepository.existsByEmail(requestDTO.getEmail())) {
            throw new BadRequestException("Email Exists!");
        }
    }

}