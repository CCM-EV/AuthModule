package com.xdpmhdt.authmodule.dto;

import com.xdpmhdt.authmodule.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Registration request DTO for Carbon Credit Marketplace
 * Supports registration for EV_OWNER, CC_BUYER, CVA, and ADMIN roles
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Username can only contain letters, numbers and underscore")
    private String username;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number and one special character"
    )
    private String password;

    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @NotNull(message = "Role is required")
    private Role role;

    // Common field for all roles
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Phone number must be 10-15 digits")
    private String phoneNumber;

    // Fields for EV_OWNER role
    @Size(max = 100, message = "Vehicle make must not exceed 100 characters")
    private String vehicleMake;

    @Size(max = 100, message = "Vehicle model must not exceed 100 characters")
    private String vehicleModel;

    @Size(max = 50, message = "Vehicle license plate must not exceed 50 characters")
    private String vehicleLicensePlate;

    // Fields for CC_BUYER role
    @Size(max = 200, message = "Organization name must not exceed 200 characters")
    private String organizationName;

    @Size(max = 100, message = "Tax ID must not exceed 100 characters")
    private String taxId;

    // Fields for CVA role
    @Size(max = 200, message = "Certification agency must not exceed 200 characters")
    private String certificationAgency;

    @Size(max = 100, message = "License number must not exceed 100 characters")
    private String licenseNumber;
}

