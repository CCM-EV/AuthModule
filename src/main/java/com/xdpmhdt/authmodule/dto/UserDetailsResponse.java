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
public class UserDetailsResponse {
    private UUID id;
    private String username;
    private String email;
    private Set<Role> roles;
    private boolean enabled;
    private boolean emailVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastLoginAt;
    
    // EV_OWNER specific fields
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleLicensePlate;
    
    // CC_BUYER specific fields
    private String organizationName;
    private String taxId;
    
    // CVA specific fields
    private String certificationAgency;
    private String licenseNumber;
}

