package com.yawka.tasks.service;

import com.yawka.tasks.config.JwtConfig;
import com.yawka.tasks.dto.UserResponseDto;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;

    public String generateToken(UserResponseDto userResponseDto) {
        return Jwts.builder()
                .subject(userResponseDto.getUsername())
                .claim("role", userResponseDto.getRole())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtConfig.getExpiriationDate()))
                .signWith(jwtConfig.getHmacKey())
                .compact();
    }
}
