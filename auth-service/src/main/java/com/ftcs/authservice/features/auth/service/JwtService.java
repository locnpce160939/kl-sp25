package com.ftcs.authservice.features.auth.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftcs.authservice.features.account.dto.AccountDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {
    @Value(" ${jwt.secret}")
    private String secretKey;

    @Value("${jwt.token.expiration}")
    private long jwtExpiration;


    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public String generateToken(AccountDto accountDto) {
        return generateToken(new HashMap<>(), accountDto);
    }

    private String generateToken(Map<String, Object> claims, AccountDto account) {
        return buildToken(claims, account, jwtExpiration);
    }

    @SneakyThrows
    private String buildToken(
            Map<String, Object> claims,
            AccountDto accountDto,
            long expiration
    ) {
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(accountDto);
        return Jwts.builder()
                .claims(claims)
                .claim("account", mapper.readValue(json, AccountDto.class))
                .subject(accountDto.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
