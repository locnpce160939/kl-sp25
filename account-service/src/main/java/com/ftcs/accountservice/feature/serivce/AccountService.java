package com.ftcs.accountservice.feature.serivce;

import com.ftcs.accountservice.feature.account.dto.*;
import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.common.exception.BadRequestException;
import com.ftcs.common.feature.service.SendMailService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.json.JSONObject;
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

    public void registerSendUser(RegisterDTORequest registerDTORequest, HttpServletRequest request) {
        if (accountRepository.existsByUsername(registerDTORequest.getUsername())) {
            throw new BadRequestException("Username Exists!");
        }
        if (accountRepository.existsByEmail(registerDTORequest.getEmail())) {
            throw new BadRequestException("Email Exists!");
        }
        if (!registerDTORequest.getRole().equals("customer") && !registerDTORequest.getRole().equals("driver")) {
            throw new BadRequestException("Invalid role!");

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
            account.setPassword(passwordEncoder.encode(registerConfirmDTORequest.getPassword()));
            account.setPhone(registerConfirmDTORequest.getPhone());
            account.setStatus("Active");
            if (!registerConfirmDTORequest.getRole().equals("customer") && !registerConfirmDTORequest.getRole().equals("driver")) {
                throw new BadRequestException("Invalid role!");

            }else{
                account.setRole(registerConfirmDTORequest.getRole());
            }
            accountRepository.save(account);
            return account;
        }
    }

    public Account createNewAccount(RegisterDTORequest registerDTORequest) {
        if (accountRepository.existsByUsername(registerDTORequest.getUsername())) {
            throw new BadRequestException("Username Exists!");
        }
        if (accountRepository.existsByEmail(registerDTORequest.getEmail())) {
            throw new BadRequestException("Email Exists!");
        }
        Account account = new Account();
        account.setUsername(registerDTORequest.getUsername());
        account.setPassword(passwordEncoder.encode(registerDTORequest.getPassword()));
        account.setPhone(registerDTORequest.getPhone());
        account.setRole(registerDTORequest.getRole());
        account.setEmail(registerDTORequest.getEmail());
        account.setStatus("Active");
        accountRepository.save(account);
        return account;
    }

    public void deleteAccount(Integer id) {
        if (!accountRepository.existsById(id)) {
            throw new BadRequestException("Account does not exist!");
        } else {
            Account account = accountRepository.findAccountByAccountId(id);
            account.setStatus("isDisabled");
            accountRepository.save(account);
        }
    }

    public Account profile(Integer id){
        Account account = accountRepository.findAccountByAccountId(id);
        account.setPassword("");
        return account;
    }

    public Account updateProfile(UpdateProfileDTORequest updateProfileDTORequest, Integer accountId) {
        Account account = accountRepository.findAccountByAccountId(accountId);
        if (account == null){
            throw new BadRequestException("Account does not exist!");
        }else{
            account.setPhone(updateProfileDTORequest.getPhone());
            accountRepository.save(account);
            return account;
        }
    }

    public List<Account> getAllAccount(){
        List<Account> accounts = accountRepository.findAll();
        accounts.forEach(account -> account.setPassword(""));
        if (accounts.isEmpty()) {
            throw new BadRequestException("Do not have any Account!");
        } else {
            return accountRepository.findAll();
        }
    }

    public Account getAccountById(Integer id) {
        if (!accountRepository.existsById(id)) {
            throw new BadRequestException("Account does not exist!");
        }
        return accountRepository.findAccountByAccountId(id);
    }

    public void forgotPasswordAccountSend(ForgotPasswordAccountDTORequest forgotPasswordAccountDTORequest, HttpServletRequest request) {
        if (!accountRepository.existsByEmail(forgotPasswordAccountDTORequest.getEmail())) {
            throw new BadRequestException("Email does not exist!");
        } else {
            int randomNumber = new Random().nextInt(900000) + 100000;
            String subject = "OTP authentication forgot password";
            sendMailService.send_otp(forgotPasswordAccountDTORequest.getEmail(), subject, randomNumber);
            request.getSession().setAttribute("code_forgot", String.valueOf(randomNumber));
        }
    }

    public void forgotPasswordAccountConfirm(ForgotPasswordAccountDTORequest forgotPasswordAccountDTORequest, HttpServletRequest request) {
        if (forgotPasswordAccountDTORequest.getOtp().equals(request.getSession().getAttribute("code_forgot"))) {
            if (forgotPasswordAccountDTORequest.getNewPassword().equals(forgotPasswordAccountDTORequest.getConfirmPassword())) {
                Account account = accountRepository.findAccountByEmail(forgotPasswordAccountDTORequest.getEmail());
                account.setPassword(passwordEncoder.encode(forgotPasswordAccountDTORequest.getNewPassword()));
                accountRepository.save(account);
            } else {
                throw new BadRequestException("RePassword not match!");
            }
        } else {
            throw new BadRequestException("Invalid OTP!");
        }
    }

    public void changePasswordAccount(ChangePasswordDTORequest changePasswordDTORequest, Integer accountId) {
       Account account = accountRepository.findAccountByAccountId(accountId);
        if (passwordEncoder.matches(changePasswordDTORequest.getOldPassword(), account.getPassword())) {
            if (changePasswordDTORequest.getNewPassword().equals(changePasswordDTORequest.getConfirmPassword())) {
                account.setPassword(passwordEncoder.encode(changePasswordDTORequest.getNewPassword()));
                accountRepository.save(account);
            } else {
                throw new BadRequestException("RePassword not match!");
            }
        } else {
            throw new BadRequestException("Old password is not correct!");
        }
    }
}
