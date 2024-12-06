package com.ftcs.authservice.configs.filters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ftcs.common.exception.AppException;
import com.ftcs.common.exception.UnauthorizedException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Map;


@Component
@AllArgsConstructor
@NoArgsConstructor
public class ParseToken {

    @Value("${jwt.secret}")
    private String jwtSecret;

    public Integer getId(String token) {
        try {
            String tokenWithoutBearer = token.substring(7);
            Claims claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(tokenWithoutBearer)
                    .getPayload();

            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(claims.get("account", Map.class).get("id"), Integer.class);
        } catch (Exception e) {
            throw new UnauthorizedException("Token is expired");
        }
    }

    public String getUsername(String token) {
        try {
            String tokenWithoutBearer = token.substring(7);
            Claims claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(tokenWithoutBearer)
                    .getPayload();

            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(claims.get("account", Map.class).get("username"), String.class);
        } catch (Exception e) {
            throw new UnauthorizedException("Token is expired");
        }
    }

    public String getRole(String token) {
        try {
            String tokenWithoutBearer = token.substring(7);
            Claims claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(tokenWithoutBearer)
                    .getPayload();

            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(claims.get("account", Map.class).get("role"), String.class);
        } catch (Exception e) {
            throw new UnauthorizedException("Token is expired");
        }
    }



    public String getEmployeeFullCode(String token) {
        try {
            String tokenWithoutBearer = token.substring(7);
            Claims claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(tokenWithoutBearer)
                    .getPayload();

            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(claims.get("account", Map.class).get("employeeFullCode"), String.class);
        } catch (Exception e) {
            throw new UnauthorizedException("Token is expired");
        }
    }

    public String getEmployeeFullName(String token) {
        try {
            String tokenWithoutBearer = token.substring(7);
            Claims claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(tokenWithoutBearer)
                    .getPayload();

            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(claims.get("account", Map.class).get("fullName"), String.class);
        } catch (Exception e) {
            throw new UnauthorizedException("Token is expired");
        }
    }

    public Long getIdLong(String token) {
        try {
            String tokenWithoutBearer = token.substring(7);
            Claims claims = Jwts.parser()
                    .verifyWith(getSignInKey())
                    .build()
                    .parseSignedClaims(tokenWithoutBearer)
                    .getPayload();

            ObjectMapper mapper = new ObjectMapper();
            return mapper.convertValue(claims.get("account", Map.class).get("id"), Long.class);
        } catch (Exception e) {
            throw new AppException(403, "Token is expired");
        }
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}