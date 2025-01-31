package com.ftcs.authservice.features.auth.service;

import com.ftcs.authservice.features.account.Account;
import com.ftcs.authservice.features.account.AccountRepository;
import com.ftcs.authservice.features.account.dto.AccountDto;
import com.ftcs.authservice.features.auth.dto.AuthenticationRequest;
import com.ftcs.authservice.features.auth.dto.AuthenticationResponse;
import com.ftcs.common.exception.NotFoundException;
import com.ftcs.common.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final AccountRepository accountRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;


    public AuthenticationResponse onLogin(AuthenticationRequest request) {
        Account account = accountRepository.findAccountByUsername(request.username())
                .orElseThrow(() -> new NotFoundException("Account not found for username: " + request.username()));

        if (!passwordEncoder.matches(request.password(), account.getPassword())) {
            throw new UnauthorizedException("Invalid password for username: " + request.username());
        }
        if (account.getStatus().equals("isDisabled")){
            throw new UnauthorizedException("Account is disabled");
        }else{
            account.setLastLogin(LocalDateTime.now());
            accountRepository.save(account);
            String accessToken = jwtService.generateToken(AccountDto.mapToAccountDto(account));
            return AuthenticationResponse.builder()
                    .accessToken(accessToken)
                    .userId(account.getAccountId())
                    .username(account.getUsername())
                    .fullName(account.getFullName())
                    .role(account.getRole())
                    .build();
        }
    }
}
