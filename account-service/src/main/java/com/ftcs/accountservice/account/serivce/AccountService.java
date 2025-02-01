package com.ftcs.accountservice.account.serivce;

import com.ftcs.accountservice.account.dto.*;
import com.ftcs.accountservice.account.dto.register.RegisterConfirmRequestDTO;
import com.ftcs.accountservice.account.dto.register.RegisterRequestDTO;
import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.authservice.features.account.contacts.RoleType;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.common.exception.NotFoundException;
import com.ftcs.common.service.SendMailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
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
        account.setStatus("Active");
        account.setRole(requestDTO.getRole());
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
        account.setStatus("Active");
        return accountRepository.save(account);
    }

    public void deleteAccount(Integer id) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new BadRequestException("Account does not exist!"));
        account.setStatus("isDisabled");
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

    public List<Account> getAllAccounts() {
        List<Account> accounts = accountRepository.findAll();
        if (accounts.isEmpty()) {
            throw new BadRequestException("No accounts found!");
        }
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

    public List<Account> findAllByRole(String role) {
        RoleType roleType;
        try {
            roleType = RoleType.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid role: " + role);
        }

        List<Account> accounts = accountRepository.findAllByRole(roleType);
        if (accounts.isEmpty()) {
            throw new NotFoundException("No accounts found for the role: " + roleType);
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