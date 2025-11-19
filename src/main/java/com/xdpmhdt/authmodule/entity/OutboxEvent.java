package com.xdpmhdt.authmodule.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "outbox_events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OutboxEvent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String eventId;
    
    @Column(nullable = false)
    private String eventType;
    
    @Column(nullable = false)
    private String routingKey;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column
    private LocalDateTime publishedAt;
    
    @Column(nullable = false)
    private Boolean published = false;
    
    @Column
    private Integer retryCount = 0;
    
    @Column
    private String errorMessage;
}
