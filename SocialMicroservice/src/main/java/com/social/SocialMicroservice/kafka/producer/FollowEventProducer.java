package com.social.SocialMicroservice.kafka.producer;

import com.social.SocialMicroservice.entities.FollowRequest;
import com.social.SocialMicroservice.entities.Followers;
import com.social.SocialMicroservice.kafka.dto.FollowAnsweredEvent;
import com.social.SocialMicroservice.kafka.dto.FollowRequestEvent;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * REAJUSTAR!! HAY FALLOS
 *
 * Kafka Producer: here we produce the messages that are going to be sent
 * Circuit Breaker is included in case Kafka fails so the Microservice doesn't stop running
 */
@Service @Log4j2
public class FollowEventProducer {

    private static final String KAFKA_PRODUCER = "kafkaProducer";

    //The KafkaTemplate created in configuration to send messages to topics using the configured fabric
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${topic.follow-requests}")
    private String topic;

    @Value("${topic.follow-answers}")
    private String topic2;

    public FollowEventProducer(@Qualifier("kafkaTemplate")KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Instance of publishFollowRequestEvent object that will be past as the event
     * with CircuitBreaker and Retry (from Resiliance4j)
     *
     * - CompletableFuture: allows Resilience4j to correctly manage the timeouts and async operations
     *
     * - CircuitBreaker opens a circuit when there are many fails
     * - Retry: automatically retries with exponential backoff
     * - TimeLimiter: establishes timeout to the operations
     *
     * Fallback methods are executed when circuit breaker is open
     */
    @CircuitBreaker(name = KAFKA_PRODUCER, fallbackMethod = "followRequestFallback")
    @Retry(name = KAFKA_PRODUCER)
    @TimeLimiter(name = KAFKA_PRODUCER)
    public CompletableFuture<SendResult<String, Object>> publishFollowRequestEvent(FollowRequest fr) {
        FollowRequestEvent event = new FollowRequestEvent(
                fr.getId(),
                fr.getFollowerId(),
                fr.getFollowedId(),
                fr.getStatus(),
                fr.getCreatedAt(),
                fr.getUpdatedAt()
        );

        log.info("Sending FollowRequestEvent for followedId: {}", event.followedId());

        return kafkaTemplate.send(topic, event.followedId().toString(), event)
                .thenApply(result -> {
                    log.info("Event successfully sent: {}", result.getRecordMetadata());
                    return result;
                })
                .exceptionally(ex -> {
                    log.error("Error at sending FollowRequestEvent", ex);
                    throw new RuntimeException("Error sending the event to Kafka", ex);
                });

    }

    @CircuitBreaker(name = KAFKA_PRODUCER, fallbackMethod = "followAnswerFallback")
    @Retry(name = KAFKA_PRODUCER)
    @TimeLimiter(name = KAFKA_PRODUCER)
    public CompletableFuture<SendResult<String, Object>> publishFollowAnswerEvent(Followers fs){

        FollowAnsweredEvent event = new FollowAnsweredEvent(
                fs.getId(),
                fs.getFollowedId(),
                fs.getFollowerId(),
                fs.getFollowedSince()
        );

        log.info("Sending FollowAnsweredEvent for followerRegisterId: {}", event.followerRegisterId());

        return kafkaTemplate.send(topic2, event.followerRegisterId().toString(), event)
                .thenApply(result -> {
                    log.info("Response event sent successfully: {}", result.getRecordMetadata());
                    return result;
                })
                .exceptionally(ex -> {
                    log.error("Error sending FollowAnsweredEvent", ex);
                    throw new RuntimeException("Error sending the event to Kafka", ex);
                });
    }

    // ==================== FALLBACK METHODS ====================

    /**
     * Fallback when publishFollowRequestEvent fails
     */
    private CompletableFuture<SendResult<String, Object>> followRequestFallback(FollowRequest fr, Exception ex) {
        log.error("Circuit Breaker OPENED for FollowRequest. ID: {}, Error: {}",
                fr.getId(), ex.getMessage());

        // Right now we manage the outbox mechanism from the service, nut we could implement it here

        // Here you can implement alternative logic:
        // - Save to database for later processing
        // - Send to dead letter queue
        // - Notify a monitoring system

        // For now, we're returning a failed CompletableFuture
        CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(
                new RuntimeException("Kafka not available. Event not sent: " + fr.getId(), ex)
        );
        return failedFuture;
    }

    /**
     * Fallback when publishFollowAnswerEvent fails
     */
    private CompletableFuture<SendResult<String, Object>> followAnswerFallback(Followers fs, Exception ex) {
        log.error("Circuit Breaker OPENED for FollowAnswer. ID: {}, Error: {}",
                fs.getId(), ex.getMessage());

        // Implement a similar logic as the previous one
        CompletableFuture<SendResult<String, Object>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(
                new RuntimeException("Kafka not available. Event not sent: " + fs.getId(), ex)
        );
        return failedFuture;
    }
}

