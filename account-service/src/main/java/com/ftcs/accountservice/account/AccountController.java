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
}
