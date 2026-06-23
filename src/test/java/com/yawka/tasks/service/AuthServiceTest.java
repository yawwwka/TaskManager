package com.yawka.tasks.service;

import com.yawka.tasks.dto.AuthRequest;
import com.yawka.tasks.dto.AuthResponse;
import com.yawka.tasks.dto.RefreshRequest;
import com.yawka.tasks.dto.RefreshResponse;
import com.yawka.tasks.dto.RegisterRequest;
import com.yawka.tasks.dto.UserResponseDto;
import com.yawka.tasks.entity.Role;
import com.yawka.tasks.entity.UserEntity;
import com.yawka.tasks.exception.InvalidPasswordException;
import com.yawka.tasks.exception.InvalidTokenException;
import com.yawka.tasks.exception.UsernameAlreadyExistsException;
import com.yawka.tasks.mapper.UserMapper;
import com.yawka.tasks.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private JwtService jwtService;

    @Mock
    private JwtHelper jwtHelper;

    @Mock
    private UserHelper userHelper;

    @InjectMocks
    private AuthService authService;

    @Test
    void shouldRegisterNewUser() {
        // given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("newuser");
        request.setPassword("password123");

        UserEntity entity = new UserEntity();
        entity.setUsername("newuser");
        entity.setPassword("password123");  // ← Исправлено! Должен быть тот же пароль
        entity.setRole(Role.USER);

        UserEntity savedEntity = new UserEntity();
        savedEntity.setId(1L);
        savedEntity.setUsername("newuser");
        savedEntity.setRole(Role.USER);

        UserResponseDto responseDto = UserResponseDto.builder()
                .id(1L)
                .username("newuser")
                .role(Role.USER)
                .build();

        when(userRepository.existsByUsername("newuser")).thenReturn(false);
        when(userMapper.toEntity(request)).thenReturn(entity);
        when(passwordEncoder.encode("password123")).thenReturn("encoded_password");  // ← То же значение!
        when(userRepository.save(entity)).thenReturn(savedEntity);
        when(userMapper.toDto(savedEntity)).thenReturn(responseDto);

        // when
        UserResponseDto result = authService.register(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getUsername()).isEqualTo("newuser");
        assertThat(result.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        // given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("existinguser");
        request.setPassword("password123");

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // when/then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(UsernameAlreadyExistsException.class)
                .hasMessageContaining("Username already exists");
    }

    @Test
    void shouldLoginSuccessfully() {
        // given
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setPassword("encoded_password");
        user.setRole(Role.USER);

        UserResponseDto userDto = UserResponseDto.builder()
                .id(1L)
                .username("testuser")
                .role(Role.USER)
                .build();

        when(userHelper.getUserByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("password123", "encoded_password")).thenReturn(true);
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(jwtService.generateAccessToken(userDto)).thenReturn("access_token");
        when(jwtService.generateRefreshToken(userDto)).thenReturn("refresh_token");

        // when
        AuthResponse response = authService.login(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("access_token");
        assertThat(response.getRefreshToken()).isEqualTo("refresh_token");
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void shouldThrowExceptionWhenInvalidPassword() {
        // given
        AuthRequest request = new AuthRequest();
        request.setUsername("testuser");
        request.setPassword("wrongpassword");

        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPassword("encoded_password");

        when(userHelper.getUserByUsername("testuser")).thenReturn(user);
        when(passwordEncoder.matches("wrongpassword", "encoded_password")).thenReturn(false);

        // when/then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidPasswordException.class)
                .hasMessageContaining("Invalid password");
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        // given
        RefreshRequest request = new RefreshRequest();
        request.setRefreshToken("valid_refresh_token");

        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setUsername("testuser");
        user.setRole(Role.USER);

        UserResponseDto userDto = UserResponseDto.builder()
                .id(1L)
                .username("testuser")
                .role(Role.USER)
                .build();

        when(jwtHelper.extractType("valid_refresh_token")).thenReturn("refresh");
        when(jwtHelper.isTokenValid("valid_refresh_token")).thenReturn(true);
        when(jwtHelper.isTokenExpired("valid_refresh_token")).thenReturn(false);
        when(jwtHelper.extractUsername("valid_refresh_token")).thenReturn("testuser");
        when(userHelper.getUserByUsername("testuser")).thenReturn(user);
        when(userMapper.toDto(user)).thenReturn(userDto);
        when(jwtService.generateAccessToken(userDto)).thenReturn("new_access_token");
        when(jwtService.generateRefreshToken(userDto)).thenReturn("new_refresh_token");

        // when
        RefreshResponse response = authService.refresh(request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isEqualTo("new_access_token");
        assertThat(response.getRefreshToken()).isEqualTo("new_refresh_token");
    }

    @Test
    void shouldThrowExceptionWhenRefreshTokenIsExpired() {
        // given
        RefreshRequest request = new RefreshRequest();
        request.setRefreshToken("expired_refresh_token");

        when(jwtHelper.extractType("expired_refresh_token")).thenReturn("refresh");
        when(jwtHelper.isTokenValid("expired_refresh_token")).thenReturn(true);
        when(jwtHelper.isTokenExpired("expired_refresh_token")).thenReturn(true);

        // when/then
        assertThatThrownBy(() -> authService.refresh(request))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessageContaining("Refresh token has expired");
    }
}