package com.xdpmhdt.authmodule.repository;

import com.xdpmhdt.authmodule.entity.AuditLog;
import com.xdpmhdt.authmodule.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    
    List<AuditLog> findByUser(User user);
    
    List<AuditLog> findByUserOrderByCreatedAtDesc(User user);
    
    List<AuditLog> findByAction(String action);
    
    List<AuditLog> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    
    List<AuditLog> findByUserAndCreatedAtBetween(User user, LocalDateTime start, LocalDateTime end);
}

