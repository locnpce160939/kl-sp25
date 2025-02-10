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
    public ApiResponse<?> registerSendUser(@Valid @RequestBody RegisterRequestDTO requestDTO, HttpServletRequest request) {
        accountService.registerSendUser(requestDTO, request);
        return new ApiResponse<>("Send mail success");
    }

    @PostMapping("/register/confirm")
    public ApiResponse<?> registerConfirmUser(@Valid @RequestBody RegisterConfirmRequestDTO requestDTO, HttpServletRequest request) {
        return new ApiResponse<>(accountService.registerConfirmUser(requestDTO, request));
    }

    @PutMapping("/isDisable/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<?> deleteAccount(@Valid @PathVariable("accountId") Integer accountId) {
        accountService.deleteAccount(accountId);
        return new ApiResponse<>("Delete account success");
    }

    @PostMapping("/createAccount")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<?> createNewAccount(@Valid @RequestBody RegisterRequestDTO requestDTO) {
        return new ApiResponse<>(accountService.createNewAccount(requestDTO));
    }

    @GetMapping("/profile")
    public ApiResponse<?> getProfile(@RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(accountService.profile(accountId));
    }

    @PutMapping("/editProfile")
    public ApiResponse<?> editProfile(@Valid @RequestBody UpdateProfileRequestDTO requestDTO, @RequestAttribute("accountId") Integer accountId) {
        return new ApiResponse<>(accountService.updateProfile(requestDTO, accountId));
    }

    @GetMapping("/getAllAccount")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<?> getAllAccount() {
        return new ApiResponse<>(accountService.getAllAccounts());
    }

    @GetMapping("/getAccount/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<?> getAccount(@PathVariable("accountId") Integer accountId) {
        return new ApiResponse<>(accountService.getAccountById(accountId));
    }

    @PostMapping("/forgotSend")
    public ApiResponse<?> forgotSend(@Valid @RequestBody ForgotPasswordAccountRequestDTO requestDTO, HttpServletRequest request) {
        accountService.forgotPasswordAccountSend(requestDTO, request);
        return new ApiResponse<>("Send mail success");
    }

    @PostMapping("/forgotConfirm")
    public ApiResponse<?> forgotConfirm(@Valid @RequestBody ForgotPasswordAccountRequestDTO requestDTO, HttpServletRequest request) {
        accountService.forgotPasswordAccountConfirm(requestDTO, request);
        return new ApiResponse<>("Change password success");
    }

    @PostMapping("/changePassword")
    public ApiResponse<?> changePassword(@Valid @RequestBody ChangePasswordRequestDTO requestDTO, @RequestAttribute("accountId") Integer accountId) {
        accountService.changePasswordAccount(requestDTO, accountId);
        return new ApiResponse<>("Change password success");
    }

    @PostMapping("/findByRole")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<?> findByRole(@Valid @RequestBody FilterRequestDTO requestDTO) {
        return new ApiResponse<>(accountService.findAllByRole(requestDTO.getRole()));
    }

    @PutMapping("/editAccount/{accountId}")
    @PreAuthorize("hasPermission(null, 'ADMIN') or hasPermission(null, 'HR')")
    public ApiResponse<?> editAccount(@Valid @RequestBody RegisterRequestDTO requestDTO,
                                      @PathVariable("accountId") Integer accountId) {
        return new ApiResponse<>(accountService.updateAccount(accountId, requestDTO));
    }
}
