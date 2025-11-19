package com.xdpmhdt.authmodule.dto;

import com.xdpmhdt.authmodule.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserListResponse {
    private UUID id;
    private String username;
    private String email;
    private Set<Role> roles;
    private boolean enabled;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private String region;
    
    // Role-specific info
    private String organizationName; // For CC_BUYER
    private String certificationAgency; // For CVA
    private String vehicleMake; // For EV_OWNER
}

