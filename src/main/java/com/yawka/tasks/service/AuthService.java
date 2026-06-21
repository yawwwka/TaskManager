package com.yawka.tasks.service;

import com.yawka.tasks.dto.AuthRequest;
import com.yawka.tasks.dto.AuthResponse;
import com.yawka.tasks.dto.RegisterRequest;
import com.yawka.tasks.dto.UserResponseDto;
import com.yawka.tasks.entity.UserEntity;
import com.yawka.tasks.exception.InvalidPasswordException;
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

    public UserResponseDto register(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername()))
            throw new UsernameAlreadyExistsException("Username already exists");

        UserEntity entity = userMapper.toEntity(registerRequest);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        UserEntity saved = userRepository.save(entity);

        return userMapper.toDto(saved);
    }

    public AuthResponse login(AuthRequest authRequest) {
        UserEntity userToLogin = userRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!passwordEncoder.matches(authRequest.getPassword(), userToLogin.getPassword()))
            throw new InvalidPasswordException("Invalid password");

        UserResponseDto userResponseDto = userMapper.toDto(userToLogin);
        String token = jwtService.generateToken(userResponseDto);

        return AuthResponse.builder()
                .token(token)
                .username(userToLogin.getUsername())
                .role(userToLogin.getRole())
                .build();
    }
}
