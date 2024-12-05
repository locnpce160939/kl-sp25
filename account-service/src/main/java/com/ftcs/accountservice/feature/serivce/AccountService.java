package com.ftcs.accountservice.feature.serivce;

import com.ftcs.accountservice.feature.account.dto.RegisterConfirmDTORequest;
import com.ftcs.accountservice.feature.account.dto.RegisterDTORequest;
import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.common.feature.service.SendMailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

@Service
@AllArgsConstructor
public class AccountService {
    private AccountRepository accountRepository;
    private SendMailService sendMailService;

    public void registerSendUser(RegisterDTORequest registerDTORequest, HttpServletRequest request) {
        if (accountRepository.existsByUsername(registerDTORequest.getUsername())) {
            throw new BadRequestException("Username Exists!");
        }
        if (accountRepository.existsByEmail(registerDTORequest.getEmail())) {
            throw new BadRequestException("Email Exists!");
        }
        if (registerDTORequest.getPassword() == null || registerDTORequest.getPhone() == null ||
                registerDTORequest.getEmail() == null || registerDTORequest.getUsername() == null) {
            throw new BadRequestException("Missing Information!");
        }
        int randomNumber = new Random().nextInt(900000) + 100000;
        String subject = "OTP authentication";
        sendMailService.send_otp(registerDTORequest.getEmail(), subject, randomNumber);
        request.getSession().setAttribute("code_register", String.valueOf(randomNumber));
    }

    public Account registerConfirmUser(RegisterConfirmDTORequest registerConfirmDTORequest, HttpServletRequest request) {
        String otp = registerConfirmDTORequest.getOtp();
        String sessionOtp = (String) request.getSession().getAttribute("code_register");
        if (!otp.equals(sessionOtp)) {
            throw new BadRequestException("Invalid OTP!");
        }else {
            Account account = new Account();
            account.setUsername(registerConfirmDTORequest.getUsername());
            account.setEmail(registerConfirmDTORequest.getEmail());
            account.setPassword(hashString(registerConfirmDTORequest.getPassword()));
            account.setPhone(registerConfirmDTORequest.getPhone());
            account.setRole("customer");
            accountRepository.save(account);
            return account;
        }
    }

    public String hashString(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(
                    input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
