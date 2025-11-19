package com.social.SocialMicroservice.kafka.producer;

import com.social.SocialMicroservice.entities.FollowRequest;
import com.social.SocialMicroservice.kafka.dto.FollowRequestEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class FollowEventProducer {

    private final KafkaTemplate<String, FollowRequestEvent> kafkaTemplate;

    @Value("${topic.follow-requests}")
    private String topic;

    public FollowEventProducer(KafkaTemplate<String, FollowRequestEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishFollowRequestEvent(FollowRequest fr) {
        FollowRequestEvent event = new FollowRequestEvent(
                fr.getId(),
                fr.getFollowerId(),
                fr.getFollowedId(),
                fr.getStatus(),
                fr.getCreatedAt(),
                fr.getUpdatedAt()
        );

        kafkaTemplate.send(topic, event);
    }
}

