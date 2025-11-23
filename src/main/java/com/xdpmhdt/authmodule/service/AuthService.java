package com.xdpmhdt.authmodule.service;

import com.xdpmhdt.authmodule.dto.AuthResponse;
import com.xdpmhdt.authmodule.dto.LoginRequest;
import com.xdpmhdt.authmodule.dto.RegisterRequest;
import com.xdpmhdt.authmodule.entity.RefreshToken;
import com.xdpmhdt.authmodule.entity.Role;
import com.xdpmhdt.authmodule.entity.User;
import com.xdpmhdt.authmodule.entity.VerificationToken;
import com.xdpmhdt.authmodule.exception.BadRequestException;
import com.xdpmhdt.authmodule.exception.UnauthorizedException;
import com.xdpmhdt.authmodule.repository.RefreshTokenRepository;
import com.xdpmhdt.authmodule.repository.UserRepository;
import com.xdpmhdt.authmodule.repository.VerificationTokenRepository;
import com.xdpmhdt.authmodule.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final OutboxEventPublisher outboxEventPublisher;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        // Check if username already exists
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new BadRequestException("Username is already taken");
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email is already registered");
        }

        // Create new user
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(request.getRole() != null ? request.getRole() : Role.EV_OWNER);
        // TODO: Flip back to false once email verification flow is enabled
        user.setEnabled(true); // Allow login until email verification feature is completed
        user.setCreatedAt(LocalDateTime.now());

        // Set common fields
        user.setPhoneNumber(request.getPhoneNumber());
        user.setRegion(request.getRegion());

        // Set role-specific fields
        switch (request.getRole()) {
            case EV_OWNER:
                user.setVehicleMake(request.getVehicleMake());
                user.setVehicleModel(request.getVehicleModel());
                user.setVehicleLicensePlate(request.getVehicleLicensePlate());
                break;
            case CC_BUYER:
                user.setOrganizationName(request.getOrganizationName());
                user.setTaxId(request.getTaxId());
                break;
            case CVA:
                user.setCertificationAgency(request.getCertificationAgency());
                user.setLicenseNumber(request.getLicenseNumber());
                break;
            case ADMIN:
                // Admin doesn't require additional fields
                break;
        }

        // Save user
        user = userRepository.save(user);

        // Publish user registration event using standardized DTO
        com.xdpmhdt.authmodule.event.dto.UserEventDTO event = com.xdpmhdt.authmodule.event.dto.UserEventDTO.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType("USER_REGISTERED")
                .source("auth-service")
                .timestamp(java.time.OffsetDateTime.now())
                .version("1.0")
                .correlationId(UUID.randomUUID().toString())
                .userId(String.valueOf(user.getId()))
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .region(user.getRegion())
                .enabled(user.isEnabled())
                .action("REGISTERED")
                .organizationName(user.getOrganizationName())
                .vehicleMake(user.getVehicleMake())
                .vehicleModel(user.getVehicleModel())
                .phoneNumber(user.getPhoneNumber())
                .build();
        outboxEventPublisher.saveEvent("USER_REGISTERED", "auth.user.registered", event);

        // Create verification token
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken(UUID.randomUUID().toString());
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24));
        verificationTokenRepository.save(verificationToken);

        // TODO: Send verification email
        // emailService.sendVerificationEmail(user.getEmail(), verificationToken.getToken());

        // Generate JWT tokens (allow login even without verification for now)
        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String accessToken = jwtUtil.generateAccessToken(userDetails);
        String refreshToken = jwtUtil.generateRefreshToken(userDetails);

        // Save refresh token
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setExpiryDate(LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(refreshTokenEntity);

        // Build response
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(900000L) // 15 minutes in milliseconds
                .user(AuthResponse.UserDto.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .role(user.getRole().name())
                        .emailVerified(user.isEnabled())
                        .build())
                .build();
    }

    @Transactional
    public AuthResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );

            // Load user details
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new UnauthorizedException("User not found"));

            // Publish user login event using standardized DTO
            String ipAddress = httpRequest.getRemoteAddr();
            String userAgent = httpRequest.getHeader("User-Agent");

            com.xdpmhdt.authmodule.event.dto.UserEventDTO loginEvent = com.xdpmhdt.authmodule.event.dto.UserEventDTO.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType("USER_LOGGED_IN")
                    .source("auth-service")
                    .timestamp(java.time.OffsetDateTime.now())
                    .version("1.0")
                    .correlationId(UUID.randomUUID().toString())
                    .userId(String.valueOf(user.getId()))
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().name())
                .region(user.getRegion())
                    .enabled(user.isEnabled())
                    .action("LOGGED_IN")
                    .ipAddress(ipAddress)
                    .userAgent(userAgent)
                .phoneNumber(user.getPhoneNumber())
                    .build();
            outboxEventPublisher.saveEvent("USER_LOGIN", "auth.user.loggedin", loginEvent);

            // Generate JWT tokens
            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            // Save refresh token
            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setUser(user);
            refreshTokenEntity.setToken(refreshToken);
            refreshTokenEntity.setExpiryDate(LocalDateTime.now().plusDays(7));
            refreshTokenRepository.save(refreshTokenEntity);

            // Build response
            return AuthResponse.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(900000L) // 15 minutes in milliseconds
                    .user(AuthResponse.UserDto.builder()
                            .id(user.getId())
                            .username(user.getUsername())
                            .email(user.getEmail())
                            .firstName(user.getFirstName())
                            .lastName(user.getLastName())
                            .role(user.getRole().name())
                            .emailVerified(user.isEnabled())
                            .build())
                    .build();

        } catch (AuthenticationException e) {
            throw new UnauthorizedException("Invalid username or password");
        }
    }

     public AuthResponse.UserDto getCurrentUser(Authentication authentication) {
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new UnauthorizedException("User not authenticated");
        }

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        return AuthResponse.UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRole().name())
                .emailVerified(user.isEnabled())
                .build();
    }
}

