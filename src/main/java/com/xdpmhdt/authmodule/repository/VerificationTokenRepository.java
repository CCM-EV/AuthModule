package com.xdpmhdt.authmodule.repository;

import com.xdpmhdt.authmodule.entity.VerificationToken;
import com.xdpmhdt.authmodule.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    
    Optional<VerificationToken> findByToken(String token);
    
    Optional<VerificationToken> findByUser(User user);
    
    void deleteByUser(User user);
    
    @Modifying
    @Query("DELETE FROM VerificationToken vt WHERE vt.expiryDate < :now")
    void deleteExpiredTokens(LocalDateTime now);
}

