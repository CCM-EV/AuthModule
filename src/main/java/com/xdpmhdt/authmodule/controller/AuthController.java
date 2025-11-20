package com.xdpmhdt.authmodule.controller;

import com.xdpmhdt.authmodule.dto.AuthResponse;
import com.xdpmhdt.authmodule.dto.LoginRequest;
import com.xdpmhdt.authmodule.dto.RegisterRequest;
import com.xdpmhdt.authmodule.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "Authentication management APIs for Carbon Credit Marketplace")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a new user",
            description = "Create a new user account for Carbon Credit Marketplace (EV_OWNER, CC_BUYER, CVA, or ADMIN)"
    )
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Authenticate user and return JWT tokens for accessing Carbon Credit Marketplace"
    )
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get current user profile",
            description = "Get the authenticated user's profile information",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    public ResponseEntity<AuthResponse.UserDto> getCurrentUser(Authentication authentication) {
        AuthResponse.UserDto userDto = authService.getCurrentUser(authentication);
        return ResponseEntity.ok(userDto);
    }
}

