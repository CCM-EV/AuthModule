package com.xdpmhdt.authmodule.controller;

import com.xdpmhdt.authmodule.dto.ApiResponse;
import com.xdpmhdt.authmodule.dto.UserDetailsResponse;
import com.xdpmhdt.authmodule.dto.UserListResponse;
import com.xdpmhdt.authmodule.dto.UserUpdateRequest;
import com.xdpmhdt.authmodule.service.AdminService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "Admin management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "Get all users with filtering and pagination")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<ApiResponse<Page<UserListResponse>>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir
    ) {
        Page<UserListResponse> users = adminService.getAllUsers(page, size, role, enabled, search, sortBy, sortDir);
        return ResponseEntity.ok(ApiResponse.success(users));
    }

    @Operation(summary = "Get user details by ID")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserDetailsResponse>> getUserDetails(@PathVariable UUID userId) {
        UserDetailsResponse userDetails = adminService.getUserDetails(userId);
        return ResponseEntity.ok(ApiResponse.success(userDetails));
    }

    @Operation(summary = "Update user information")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<UserDetailsResponse>> updateUser(
            @PathVariable UUID userId,
            @RequestBody UserUpdateRequest request
    ) {
        UserDetailsResponse updatedUser = adminService.updateUser(userId, request);
        return ResponseEntity.ok(ApiResponse.success(updatedUser));
    }

    @Operation(summary = "Enable/disable user account")
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/users/{userId}/status")
    public ResponseEntity<ApiResponse<String>> toggleUserStatus(
            @PathVariable UUID userId,
            @RequestParam boolean enabled
    ) {
        adminService.toggleUserStatus(userId, enabled);
        String message = enabled ? "User enabled successfully" : "User disabled successfully";
        return ResponseEntity.ok(ApiResponse.success(message));
    }

    @Operation(summary = "Delete user account")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{userId}")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable UUID userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully"));
    }

    @Operation(summary = "Get user statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/statistics/users")
    public ResponseEntity<ApiResponse<?>> getUserStatistics() {
        var stats = adminService.getUserStatistics();
        return ResponseEntity.ok(ApiResponse.success(stats));
    }

    @Operation(summary = "Get recent user activities")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/activities")
    public ResponseEntity<ApiResponse<?>> getRecentActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        var activities = adminService.getRecentActivities(page, size);
        return ResponseEntity.ok(ApiResponse.success(activities));
    }

    @Operation(summary = "Search users by criteria")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/search")
    public ResponseEntity<ApiResponse<Page<UserListResponse>>> searchUsers(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Page<UserListResponse> users = adminService.searchUsers(query, page, size);
        return ResponseEntity.ok(ApiResponse.success(users));
    }
}

