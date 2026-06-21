package com.yawka.tasks.dto;

import com.yawka.tasks.entity.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String username;
    private Role role;
}
