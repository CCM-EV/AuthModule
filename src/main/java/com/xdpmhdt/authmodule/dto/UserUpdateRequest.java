package com.xdpmhdt.authmodule.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateRequest {
    private String email;
    private Boolean enabled;
    private Boolean emailVerified;
    private Set<String> roles;
    private String region;
    
    // EV_OWNER specific
    private String vehicleMake;
    private String vehicleModel;
    private String vehicleLicensePlate;
    
    // CC_BUYER specific
    private String organizationName;
    private String taxId;
    
    // CVA specific
    private String certificationAgency;
    private String licenseNumber;
}

