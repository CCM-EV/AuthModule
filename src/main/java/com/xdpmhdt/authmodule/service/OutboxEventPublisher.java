package com.xdpmhdt.authmodule.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xdpmhdt.authmodule.config.RabbitMQConfig;
import com.xdpmhdt.authmodule.entity.OutboxEvent;
import com.xdpmhdt.authmodule.repository.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OutboxEventPublisher {
    
    private final OutboxEventRepository outboxEventRepository;
    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public void saveEvent(String eventType, String routingKey, Object payload) {
        try {
            String payloadJson = objectMapper.writeValueAsString(payload);
            OutboxEvent event = OutboxEvent.builder()
                    .eventId(UUID.randomUUID().toString())
                    .eventType(eventType)
                    .routingKey(routingKey)
                    .payload(payloadJson)
                    .createdAt(LocalDateTime.now())
                    .published(false)
                    .retryCount(0)
                    .build();
            
            outboxEventRepository.save(event);
            log.info("Saved outbox event: {} with routing key: {}", eventType, routingKey);
        } catch (Exception e) {
            log.error("Failed to save outbox event: {}", eventType, e);
            throw new RuntimeException("Failed to save outbox event", e);
        }
    }
    
    @Scheduled(fixedDelay = 5000) // Run every 5 seconds
    @Transactional
    public void publishPendingEvents() {
        var events = outboxEventRepository.findUnpublishedEvents();
        
        for (OutboxEvent event : events) {
            try {
                Object payloadObject = objectMapper.readValue(event.getPayload(), Object.class);
                
                rabbitTemplate.convertAndSend(
                    RabbitMQConfig.EVENTS_EXCHANGE,
                    event.getRoutingKey(),
                    payloadObject
                );
                
                event.setPublished(true);
                event.setPublishedAt(LocalDateTime.now());
                outboxEventRepository.save(event);
                
                log.info("Published event: {} with routing key: {}", event.getEventType(), event.getRoutingKey());
            } catch (Exception e) {
                log.error("Failed to publish event: {}, attempt: {}", event.getEventId(), event.getRetryCount() + 1, e);
                event.setRetryCount(event.getRetryCount() + 1);
                event.setErrorMessage(e.getMessage());
                outboxEventRepository.save(event);
            }
        }
    }
}
