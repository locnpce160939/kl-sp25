package com.ftcs.accountservice.account;

import com.ftcs.accountservice.AccountURL;
import com.ftcs.common.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AccountURL.ACCOUNT)
public class AccountController {
    @GetMapping
    public ApiResponse<?> testAccountController() {
        return new ApiResponse<>("Test");
    }

}
