package com.social.SocialMicroservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.SocialMicroservice.entities.EventStatus;
import com.social.SocialMicroservice.entities.FollowRequest;
import com.social.SocialMicroservice.entities.Followers;
import com.social.SocialMicroservice.entities.OutboxEvent;
import com.social.SocialMicroservice.kafka.producer.FollowEventProducer;
import com.social.SocialMicroservice.repositories.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class reads the PENDING Outbox Events in OutboxEvent entity
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxProcessor {

    private final OutboxEventRepository outboxRepository;
    private final FollowEventProducer followEventProducer;
    private final ObjectMapper objectMapper;

    private static final int MAX_ATTEMPTS = 3;
    private static final int BATCH_SIZE = 50;
    //To manually control transactions (key to robustness).
    private final TransactionTemplate transactionTemplate;

    @Scheduled(fixedDelay = 30000) //Executes the method every 30 seconds
    public void processOutboxEvents() {
        log.info("Executing processOutboxEvent");
        Instant now = Instant.now();

        List<OutboxEvent> allPending = outboxRepository
                .findByStatusAndAttemptsLessThanAndNextRetryAtBefore(
                        EventStatus.PENDING,
                        MAX_ATTEMPTS,
                        now
                );

        List<OutboxEvent> pendingEvents = allPending.stream()
                .limit(BATCH_SIZE)
                .toList();

        //Process each event one by one with processSingleEvent.
        pendingEvents.forEach(this::processSingleEvent);
    }

    /**
     * Individual processing of each event
     */
    private void processSingleEvent(OutboxEvent event) {
        // Each event has its own transaction. Execute the entire block within a separate database transaction.
        // If this event fails, only its transaction is rolled back, not those of other events.
        // If the event is sent to Kafka, the database update (PROCESSED) is committed only if everything
        // goes well -> consistency.
        transactionTemplate.execute(status -> {
            try {
                //Reread the event from the database just before processing it. Verify that it is still in PENDING state.
                //In environments with multiple instances of the microservice, another instance might have processed
                //this event in the meantime.
                //Without this check, we would send the same event twice to Kafka -> duplicates.
                OutboxEvent freshEvent = outboxRepository.findById(event.getId())
                        .orElseThrow(() -> new IllegalStateException("Event not found: " + event.getId()));

                if (freshEvent.getStatus() != EventStatus.PENDING) {
                    log.debug("Event {} already processed", event.getId());
                    return null;
                }

                processEventPayload(freshEvent);

                // It is marked as PROCESSED
                freshEvent.setStatus(EventStatus.PROCESSED);
                outboxRepository.save(freshEvent);
                log.info("Event {} processed successfully", event.getId());

            } catch (Exception ex) {
                handleProcessingError(event, ex);
            }
            return null;
        });
    }

    /**
     * Payload processing (sending to Kafka)
     * Deserializes the JSON stored in the payload to the original Java object (FollowRequest or Followers).
     * Calls the Kafka producer and wait for its result using .join().
     */
    private void processEventPayload(OutboxEvent event) throws Exception {
        switch (event.getEventType()) {
            case "FOLLOW_REQUEST_CREATED" -> {
                FollowRequest fr = objectMapper.readValue(event.getPayload(), FollowRequest.class);
                followEventProducer.publishFollowRequestEvent(fr).join(); // waits for the answer (.join())
                // Without .join(), we wouldn't know if the submission failed, and the event would be marked as
                // PROCESSED even if Kafka didn't receive it.
                // .join() blocks until completion -> if it fails, it throws an exception that is caught in the `catch`
                // block.
            }
            case "FOLLOW_ANSWERED" -> {
                Followers fs = objectMapper.readValue(event.getPayload(), Followers.class);
                followEventProducer.publishFollowAnswerEvent(fs).join();
            }
            default -> throw new IllegalArgumentException("Unknown event type: " + event.getEventType());
        }
    }

    /**
     * Error handling and retries
     * Increments the attempt counter.
     * If it exceeds MAX_ATTEMPTS (3), marks the event as FAILED.
     * Otherwise, update attempts and save the last error.
     * Save the changes to the database.
     */
    private void handleProcessingError(OutboxEvent event, Exception ex) {
        int newAttempts = event.getAttempts() + 1;
        log.error("Error processing event {} (attempt {}). Error: {}",
                event.getId(), newAttempts, ex.getMessage());

        Instant nextRetry = calculateNextRetry(newAttempts);
        if (nextRetry == null) {
            event.setStatus(EventStatus.FAILED);
            log.error("Event {} marked as FAILED after {} attempts", event.getId(), newAttempts);
        } else {
            event.setAttempts(newAttempts);
            event.setLastError(ex.getMessage());
            event.setNextRetryAt(nextRetry);
        }
        outboxRepository.save(event);
    }

    private Instant calculateNextRetry(int attempts) {
        // Ej: 30s, 2min, 10min, 1h...
        long[] delaysInSeconds = {30, 120, 600, 3600}; // ajusta según tus necesidades
        if (attempts >= delaysInSeconds.length) {
            return null; // Ya no se reintentará → marcar como FAILED
        }
        return Instant.now().plusSeconds(delaysInSeconds[attempts]);
    }

    /*
    //This method is called every 5 seconds, so we don't need to call it
    @Scheduled(fixedDelay = 5000) //
    @Transactional
    public void processOutboxEvents() {
        //We obtain all PENDING events like in any other entity
        List<OutboxEvent> pendingEvents = outboxRepository.findByStatus(EventStatus.PENDING);

        //We filter the pending events from the List
        for (OutboxEvent event : pendingEvents) {
            try {
                // We convert the payload from the entity into its original object
                String type = event.getEventType();
                switch (type) {
                    case "FOLLOW_REQUEST_CREATED" -> {
                        //It turns the json of the object stored in the table into the object and publishes the event
                        FollowRequest fr = objectMapper.readValue(event.getPayload(), FollowRequest.class);
                        followEventProducer.publishFollowRequestEvent(fr);
                    }
                    case "FOLLOW_ANSWERED" -> {
                        Followers fs = objectMapper.readValue(event.getPayload(), Followers.class);
                        followEventProducer.publishFollowAnswerEvent(fs);
                    }
                    default -> log.warn("Unknown event in outbox: {}", type);
                }

                // We mark the status as sent "PROCESSED".
                event.setStatus(EventStatus.PROCESSED);
                outboxRepository.save(event);

            } catch (Exception ex) {
                log.error("Error procesando evento {}: {}", event.getId(), ex.getMessage());
                // Here you could increase attempts or leave it PENDING for retry
            }
        }
    }

     */
}
