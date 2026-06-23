package com.yawka.tasks.service;

import com.yawka.tasks.config.JwtConfig;
import com.yawka.tasks.dto.UserResponseDto;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import io.jsonwebtoken.Jwts;
import java.util.Date;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final JwtConfig jwtConfig;

    public String generateAccessToken(UserResponseDto userResponseDto) {
        return Jwts.builder()
                .subject(userResponseDto.getUsername())
                .claim("role", userResponseDto.getRole())
                .claim("type", "access")
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + jwtConfig.getExpirationDate()))
                .signWith(jwtConfig.getHmacKey())
                .compact();
    }

    public String generateRefreshToken(UserResponseDto userResponseDto) {
        return Jwts.builder()
                .claim("type", "refresh")
                .claim("tokenId", UUID.randomUUID().toString())
                .setSubject(userResponseDto.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getRefreshExpirationDate()))
                .signWith(jwtConfig.getHmacKey())
                .compact();
    }

}
