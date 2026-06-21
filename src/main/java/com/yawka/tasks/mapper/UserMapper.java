package com.yawka.tasks.mapper;

import com.yawka.tasks.dto.RegisterRequest;
import com.yawka.tasks.dto.UserResponseDto;
import com.yawka.tasks.entity.Role;
import com.yawka.tasks.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponseDto toDto(UserEntity userEntity) {
        return UserResponseDto.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .role(userEntity.getRole())
                .build();
    }

    public UserEntity toEntity(RegisterRequest registerRequest) {
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(registerRequest.getUsername());
        userEntity.setPassword(registerRequest.getPassword());  // ← Просто копируем
        userEntity.setRole(Role.USER);
        return userEntity;
    }
}
