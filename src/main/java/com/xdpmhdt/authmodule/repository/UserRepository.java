package com.xdpmhdt.authmodule.repository;

import com.xdpmhdt.authmodule.entity.Role;
import com.xdpmhdt.authmodule.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {  // Changed from UUID to Long
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    // Admin queries - Changed from 'roles' to 'role' to match User entity
    Page<User> findByRole(Role role, Pageable pageable);
    
    Page<User> findByEnabled(boolean enabled, Pageable pageable);
    
    Page<User> findByRoleAndEnabled(Role role, boolean enabled, Pageable pageable);
    
    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String username, String email, Pageable pageable);
    
    long countByEnabled(boolean enabled);
    
    // Note: User entity doesn't have emailVerified field currently
    // long countByEmailVerified(boolean emailVerified);
    
    long countByRole(Role role);
    
    long countByCreatedAtAfter(LocalDateTime date);
}

