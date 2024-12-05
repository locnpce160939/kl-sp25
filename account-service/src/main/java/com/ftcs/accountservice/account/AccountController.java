package com.ftcs.accountservice.account;

import com.ftcs.accountservice.AccountURL;
import com.ftcs.accountservice.feature.account.dto.RegisterConfirmDTORequest;
import com.ftcs.accountservice.feature.account.dto.RegisterDTORequest;
import com.ftcs.accountservice.feature.serivce.AccountService;
import com.ftcs.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AccountURL.ACCOUNT)
public class AccountController {
    @Autowired
    private final AccountService accountService;

    @GetMapping
    public ApiResponse<?> testAccountController() {
        return new ApiResponse<>("Test");
    }


    @PostMapping("/register/send")
    public ApiResponse<?> registerSendUser(@Valid @RequestBody RegisterDTORequest registerDTORequest, HttpServletRequest request) throws JSONException {
        return accountService.registerSendUser(registerDTORequest, request);
    }

    @PostMapping("/register/confirm")
    public ApiResponse<?> registerConfirmUser(@Valid @RequestBody RegisterConfirmDTORequest registerConfirmDTORequest, HttpServletRequest request) throws JSONException {
        return accountService.registerConfirmUser(registerConfirmDTORequest, request);
    }
}
