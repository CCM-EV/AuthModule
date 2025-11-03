package com.xdpmhdt.authmodule.outbox;

 import com.xdpmhdt.authmodule.entity.User;
 import org.springframework.amqp.rabbit.core.RabbitTemplate;
 import org.springframework.stereotype.Service;
 import lombok.RequiredArgsConstructor;
 import lombok.extern.slf4j.Slf4j;

 @Slf4j
 @Service
 @RequiredArgsConstructor
public class OutboxEventPublisher {
    
     private final RabbitTemplate rabbitTemplate;
     private static final String EXCHANGE = "co2.events";
    
    /**
     * Publish user registration event
     */
    public void publishUserRegisteredEvent(User user) {
        try {
            var event = new UserRegisteredEvent(
                java.util.UUID.randomUUID().toString(),
                1,
                java.time.Instant.now().toString(),
                "auth-service",
                new UserRegisteredEvent.Data(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().name(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getOrganizationName(),
                    null,
                    user.getCreatedAt().toString()
                )
            );

            rabbitTemplate.convertAndSend(EXCHANGE, "auth.user.registered.v1", event);
            log.info("Published user registration event for user: {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to publish user registration event", e);
        }
    }

    /**
     * Publish user login event
     */
    public void publishUserLoggedInEvent(User user, String ipAddress, String userAgent) {
        try {
            var event = new UserLoggedInEvent(
                java.util.UUID.randomUUID().toString(),
                1,
                java.time.Instant.now().toString(),
                "auth-service",
                new UserLoggedInEvent.Data(
                    user.getId(),
                    user.getUsername(),
                    ipAddress,
                    userAgent,
                    java.time.Instant.now().toString()
                )
            );

            rabbitTemplate.convertAndSend(EXCHANGE, "auth.user.loggedin.v1", event);
            log.info("Published user login event for user: {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to publish user login event", e);
        }
    }


    record UserRegisteredEvent(
        String event_id, int schema_version, String occurred_at, String producer,
        Data data
    ){
        record Data(
            long user_id, String username, String email, String role,
            String first_name, String last_name, String organization_name,
            String region, String created_at
        ){}
    }

    record UserLoggedInEvent(
        String event_id, int schema_version, String occurred_at, String producer,
        Data data
    ){
        record Data(
            long user_id, String username, String ip_address,
            String user_agent, String login_at
        ){}
    }
}
