package com.ftcs.accountservice.account.serivce;

import com.ftcs.accountservice.account.dto.*;
import com.ftcs.accountservice.account.dto.register.RegisterConfirmRequestDTO;
import com.ftcs.accountservice.account.dto.register.RegisterRequestDTO;
import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.common.exception.NotFoundException;
import com.ftcs.common.feature.service.SendMailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountService {
    private AccountRepository accountRepository;
    private SendMailService sendMailService;

    private final PasswordEncoder passwordEncoder;

    public void registerSendUser(RegisterRequestDTO registerRequestDTO, HttpServletRequest request) {
        if (accountRepository.existsByUsername(registerRequestDTO.getUsername())) {
            throw new BadRequestException("Username Exists!");
        }
        if (accountRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new BadRequestException("Email Exists!");
        }
        if (!registerRequestDTO.getRole().equals("customer") && !registerRequestDTO.getRole().equals("driver")) {
            throw new BadRequestException("Invalid role!");

        }
        int randomNumber = generateOtpCode();
        String subject = "OTP authentication";
        sendMailService.sendOtp(registerRequestDTO.getEmail(), subject, randomNumber);
        request.getSession().setAttribute("code_register", String.valueOf(randomNumber));
    }

    public Account registerConfirmUser(RegisterConfirmRequestDTO registerConfirmRequestDTO, HttpServletRequest request) {
        String sessionOtp = (String) request.getSession().getAttribute("code_register");

        if (!registerConfirmRequestDTO.getOtp().equals(sessionOtp)) {
            throw new BadRequestException("Invalid OTP!");
        }
        String role = registerConfirmRequestDTO.getRole();
        if (!role.equals("customer") && !role.equals("driver")) {
            throw new BadRequestException("Invalid role!");
        }

        Account account = new Account();
        account.setUsername(registerConfirmRequestDTO.getUsername());
        account.setEmail(registerConfirmRequestDTO.getEmail());
        account.setPassword(passwordEncoder.encode(registerConfirmRequestDTO.getPassword()));
        account.setPhone(registerConfirmRequestDTO.getPhone());
        account.setStatus("Active");
        account.setRole(role);

        return accountRepository.save(account);
    }

    public Account createNewAccount(RegisterRequestDTO registerRequestDTO) {
        if (accountRepository.existsByUsername(registerRequestDTO.getUsername())) {
            throw new BadRequestException("Username already exists!");
        }
        if (accountRepository.existsByEmail(registerRequestDTO.getEmail())) {
            throw new BadRequestException("Email already exists!");
        }
        Account account = new Account();
        account.setUsername(registerRequestDTO.getUsername());
        account.setPassword(passwordEncoder.encode(registerRequestDTO.getPassword()));
        account.setPhone(registerRequestDTO.getPhone());
        account.setRole(registerRequestDTO.getRole());
        account.setEmail(registerRequestDTO.getEmail());
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

    public Account updateProfile(UpdateProfileRequestDTO updateProfileRequestDTO, Integer accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new BadRequestException("Account does not exist!"));
        account.setPhone(updateProfileRequestDTO.getPhone());
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
        }
        account.setPassword(passwordEncoder.encode(requestDTO.getNewPassword()));
        accountRepository.save(account);
    }

    public void changePasswordAccount(ChangePasswordRequestDTO request, Integer accountId) {
        Account account = accountRepository.findAccountByAccountId(accountId);
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

    private int generateOtpCode() {
        return new Random().nextInt(900000) + 100000;
    }

}
