package com.ftcs.authservice.features.auth;


import com.ftcs.authservice.AuthAccountURL;
import com.ftcs.authservice.features.auth.dto.AuthenticationRequest;
import com.ftcs.authservice.features.auth.dto.AuthenticationResponse;
import com.ftcs.authservice.features.auth.service.AuthenticationService;
import com.ftcs.authservice.features.auth.service.LogoutService;
import com.ftcs.common.dto.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(AuthAccountURL.AUTH)
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final LogoutService logoutService;

    @PostMapping
    public ApiResponse<AuthenticationResponse> authenticate(
            @Valid @RequestBody AuthenticationRequest request
    ) {
        return new ApiResponse<>(authenticationService.onLogin(request));
    }

    @PostMapping("/logout")
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) {
        logoutService.logout(request, response, authentication);
    }


}
