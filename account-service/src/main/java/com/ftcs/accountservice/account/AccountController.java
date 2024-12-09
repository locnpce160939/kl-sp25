package com.ftcs.accountservice.account;

import com.ftcs.accountservice.AccountURL;
import com.ftcs.accountservice.account.dto.*;
import com.ftcs.accountservice.account.dto.register.RegisterConfirmRequestDTO;
import com.ftcs.accountservice.account.dto.register.RegisterRequestDTO;
import com.ftcs.accountservice.account.serivce.AccountService;
import com.ftcs.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AccountURL.ACCOUNT)
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/register/send")
    public ApiResponse<?> registerSendUser(@Valid @RequestBody RegisterRequestDTO registerRequestDTO, HttpServletRequest request) {
        accountService.registerSendUser(registerRequestDTO, request);
        return new ApiResponse<>("Send mail success");
    }

    @PostMapping("/register/confirm")
    public ApiResponse<?> registerConfirmUser(@Valid @RequestBody RegisterConfirmRequestDTO registerConfirmRequestDTO, HttpServletRequest request) {
        return new ApiResponse<>(accountService.registerConfirmUser(registerConfirmRequestDTO, request));
    }

    @DeleteMapping("/isDisable/{accountId}")
    public ApiResponse<?> deleteAccount(@Valid @PathVariable("accountId") Integer accountId) {
        accountService.deleteAccount(accountId);
        return new ApiResponse<>("Delete account success");
    }

    @PostMapping("/createAccount")
    public ApiResponse<?> createNewAccount(@Valid @RequestBody RegisterRequestDTO registerRequestDTO) {
        return new ApiResponse<>(accountService.createNewAccount(registerRequestDTO));
    }

    @GetMapping("/profile")
    public ApiResponse<?> getProfile(@RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(accountService.profile(accountId));
    }

    @PutMapping("/editProfile")
    public ApiResponse<?> editProfile(@Valid @RequestBody UpdateProfileRequestDTO updateProfileRequestDTO, @RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(accountService.updateProfile(updateProfileRequestDTO, accountId));
    }

    @GetMapping("/getAllAccount")
    public ApiResponse<?> getAllAccount() {
        return new ApiResponse<>(accountService.getAllAccounts());
    }

    @GetMapping("/getAccount/{accountId}")
    public ApiResponse<?> getAccount(@PathVariable("accountId") Integer accountId) {
        return new ApiResponse<>(accountService.getAccountById(accountId));
    }

    @PostMapping("/forgotSend")
    public ApiResponse<?> forgotSend(@Valid @RequestBody ForgotPasswordAccountRequestDTO forgotPasswordAccountRequestDTO, HttpServletRequest request) {
        accountService.forgotPasswordAccountSend(forgotPasswordAccountRequestDTO, request);
        return new ApiResponse<>("Send mail success");
    }

    @PostMapping("/forgotConfirm")
    public ApiResponse<?> forgotConfirm(@Valid @RequestBody ForgotPasswordAccountRequestDTO forgotPasswordAccountRequestDTO, HttpServletRequest request) {
        accountService.forgotPasswordAccountConfirm(forgotPasswordAccountRequestDTO, request);
        return new ApiResponse<>("Change password success");
    }

    @PostMapping("/changePassword")
    public ApiResponse<?> changePassword(@Valid @RequestBody ChangePasswordRequestDTO changePasswordRequestDTO, @RequestAttribute("accountId") Integer accountId) {
        accountService.changePasswordAccount(changePasswordRequestDTO, accountId);
        return new ApiResponse<>("Change password success");
    }

}
