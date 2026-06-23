package com.yawka.tasks.service;

import com.yawka.tasks.dto.*;
import com.yawka.tasks.entity.UserEntity;
import com.yawka.tasks.exception.InvalidPasswordException;
import com.yawka.tasks.exception.InvalidTokenException;
import com.yawka.tasks.exception.UserNotFoundException;
import com.yawka.tasks.exception.UsernameAlreadyExistsException;
import com.yawka.tasks.mapper.UserMapper;
import com.yawka.tasks.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;
    private final JwtHelper jwtHelper;
    private final UserHelper userHelper;

    public UserResponseDto register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername()))
            throw new UsernameAlreadyExistsException("Username already exists");

        UserEntity entity = userMapper.toEntity(registerRequest);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        UserEntity saved = userRepository.save(entity);

        return userMapper.toDto(saved);
    }

    public AuthResponse login(AuthRequest authRequest) {
        UserEntity userToLogin = userHelper.getUserByUsername(authRequest.getUsername());

        if (!passwordEncoder.matches(authRequest.getPassword(), userToLogin.getPassword()))
            throw new InvalidPasswordException("Invalid password");

        UserResponseDto userResponseDto = userMapper.toDto(userToLogin);
        String accessToken = jwtService.generateAccessToken(userResponseDto);
        String refreshToken = jwtService.generateRefreshToken(userResponseDto);

        return AuthResponse.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .username(userToLogin.getUsername())
                .role(userToLogin.getRole())
                .build();
    }

    public RefreshResponse refresh(RefreshRequest refreshRequest) {
        String refreshToken = refreshRequest.getRefreshToken();

        if (!jwtHelper.extractType(refreshToken).equals("refresh"))
            throw new InvalidTokenException("Invalid token type");

        if (!jwtHelper.isTokenValid(refreshToken))
            throw new InvalidTokenException("Token is not valid");

        if (jwtHelper.isTokenExpired(refreshToken))
            throw new InvalidTokenException("Refresh token has expired");

        String username = jwtHelper.extractUsername(refreshToken);

        UserEntity user = userHelper.getUserByUsername(username);

        UserResponseDto userDto = userMapper.toDto(user);
        String newAccessToken = jwtService.generateAccessToken(userDto);
        String newRefreshToken = jwtService.generateRefreshToken(userDto);

        return RefreshResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }
}
