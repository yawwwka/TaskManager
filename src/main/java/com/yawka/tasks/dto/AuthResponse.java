package com.yawka.tasks.dto;

import com.yawka.tasks.entity.Role;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class AuthResponse {
    private String token;

    private String username;

    private Role role;
}
