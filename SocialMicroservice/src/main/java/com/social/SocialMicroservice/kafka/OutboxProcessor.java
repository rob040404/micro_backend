package com.social.SocialMicroservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.SocialMicroservice.entities.EventStatus;
import com.social.SocialMicroservice.entities.FollowRequest;
import com.social.SocialMicroservice.entities.Followers;
import com.social.SocialMicroservice.entities.OutboxEvent;
import com.social.SocialMicroservice.kafka.dto.FollowAnsweredEvent;
import com.social.SocialMicroservice.kafka.dto.FollowRequestEvent;
import com.social.SocialMicroservice.kafka.producer.FollowEventProducer;
import com.social.SocialMicroservice.repositories.OutboxEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

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

    //This method is called every 5 seconds, so we don't need to call it
    @Scheduled(fixedDelay = 5000) // cada 5 segundos
    @Transactional
    public void processOutboxEvents() {
        //We obtain all PENDING events
        List<OutboxEvent> pendingEvents = outboxRepository.findByStatus(EventStatus.PENDING);

        for (OutboxEvent event : pendingEvents) {
            try {
                // We convert the payload to its original object
                String type = event.getEventType();
                switch (type) {
                    case "FOLLOW_REQUEST_CREATED" -> {
                        //It turns the json of the object stored in the table to the object and publishes the event
                        FollowRequest fr = objectMapper.readValue(event.getPayload(), FollowRequest.class);
                        followEventProducer.publishFollowRequestEvent(fr);
                    }
                    case "FOLLOW_ANSWERED" -> {
                        Followers fs = objectMapper.readValue(event.getPayload(), Followers.class);
                        followEventProducer.publisFollowAnswerEvent(fs);
                    }
                    default -> log.warn("Evento desconocido en outbox: {}", type);
                }

                // We mark the status as sent "PROCESSED".
                event.setStatus(EventStatus.PROCESSED);
                outboxRepository.save(event);

            } catch (Exception ex) {
                log.error("Error procesando evento {}: {}", event.getId(), ex.getMessage());
                // Aquí podrías incrementar intentos o dejarlo PENDING para reintento
            }
        }
    }
}
