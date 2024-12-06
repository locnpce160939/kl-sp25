package com.ftcs.accountservice.account;

import com.ftcs.accountservice.AccountURL;
import com.ftcs.accountservice.account.dto.*;
import com.ftcs.accountservice.account.dto.register.RegisterConfirmDTORequest;
import com.ftcs.accountservice.account.dto.register.RegisterDTORequest;
import com.ftcs.accountservice.account.serivce.AccountService;
import com.ftcs.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AccountURL.ACCOUNT)
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/register/send")
    public ApiResponse<?> registerSendUser(@Valid @RequestBody RegisterDTORequest registerDTORequest, HttpServletRequest request) {
        accountService.registerSendUser(registerDTORequest, request);
        return new ApiResponse<>("Send mail success");
    }

    @PostMapping("/register/confirm")
    public ApiResponse<?> registerConfirmUser(@Valid @RequestBody RegisterConfirmDTORequest registerConfirmDTORequest, HttpServletRequest request) {
        return new ApiResponse<>(accountService.registerConfirmUser(registerConfirmDTORequest, request));
    }

    @DeleteMapping("/isDisable/{accountId}")
    public ApiResponse<?> deleteAccount(@Valid @PathVariable("accountId") Integer accountId) {
        accountService.deleteAccount(accountId);
        return new ApiResponse<>("Delete account success");
    }

    @PostMapping("/createAccount")
    public ApiResponse<?> createNewAccount(@Valid @RequestBody RegisterDTORequest registerDTORequest) {
        return new ApiResponse<>(accountService.createNewAccount(registerDTORequest));
    }

    @GetMapping("/profile")
    public ApiResponse<?> getProfile(@RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(accountService.profile(accountId));
    }

    @PutMapping("/editProfile")
    public ApiResponse<?> editProfile(@Valid @RequestBody UpdateProfileDTORequest updateProfileDTORequest, @RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(accountService.updateProfile(updateProfileDTORequest, accountId));
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
    public ApiResponse<?> forgotSend(@Valid @RequestBody ForgotPasswordAccountDTORequest forgotPasswordAccountDTORequest, HttpServletRequest request) {
        accountService.forgotPasswordAccountSend(forgotPasswordAccountDTORequest, request);
        return new ApiResponse<>("Send mail success");
    }

    @PostMapping("/forgotConfirm")
    public ApiResponse<?> forgotConfirm(@Valid @RequestBody ForgotPasswordAccountDTORequest forgotPasswordAccountDTORequest, HttpServletRequest request) {
        accountService.forgotPasswordAccountConfirm(forgotPasswordAccountDTORequest, request);
        return new ApiResponse<>("Change password success");
    }

    @PostMapping("/changePassword")
    public ApiResponse<?> changePassword(@Valid @RequestBody ChangePasswordDTORequest changePasswordDTORequest, @RequestAttribute("accountId") Integer accountId) {
        accountService.changePasswordAccount(changePasswordDTORequest, accountId);
        return new ApiResponse<>("Change password success");
    }

}
