package com.yawka.tasks.mapper;

import com.yawka.tasks.dto.RegisterRequest;
import com.yawka.tasks.dto.UserResponseDto;
import com.yawka.tasks.entity.Role;
import com.yawka.tasks.entity.UserEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {

    private final UserMapper userMapper = new UserMapper();

    @Test
    void shouldMapRegisterRequestToUserEntity() {
        // given
        RegisterRequest request = new RegisterRequest();
        request.setUsername("testuser");
        request.setPassword("password123");

        // when
        UserEntity entity = userMapper.toEntity(request);

        // then
        assertThat(entity).isNotNull();
        assertThat(entity.getUsername()).isEqualTo("testuser");
        assertThat(entity.getPassword()).isEqualTo("password123");
        assertThat(entity.getRole()).isEqualTo(Role.USER);
        assertThat(entity.getId()).isNull();
    }

    @Test
    void shouldMapUserEntityToUserResponseDto() {
        // given
        UserEntity entity = new UserEntity();
        entity.setId(1L);
        entity.setUsername("testuser");
        entity.setPassword("encoded_password");
        entity.setRole(Role.USER);

        // when
        UserResponseDto dto = userMapper.toDto(entity);

        // then
        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getUsername()).isEqualTo("testuser");
        assertThat(dto.getRole()).isEqualTo(Role.USER);
    }
}