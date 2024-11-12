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
public class TestController {

    @GetMapping("/oke")
    public ApiResponse<?> get(@RequestAttribute(name = "username") String username) {
        return new ApiResponse<>(username);
    }
}
