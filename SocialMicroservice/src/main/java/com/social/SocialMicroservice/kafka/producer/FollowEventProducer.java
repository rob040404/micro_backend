package com.social.SocialMicroservice.kafka.producer;

import com.social.SocialMicroservice.entities.FollowRequest;
import com.social.SocialMicroservice.kafka.dto.FollowRequestEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class FollowEventProducer {

    //The KafkaTemplate created in configuration to send messages to topics using the configured fabric
    private final KafkaTemplate<String, FollowRequestEvent> kafkaTemplate;

    @Value("${topic.follow-requests}")
    private String topic;

    public FollowEventProducer(KafkaTemplate<String, FollowRequestEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    //Instance of publishFollowRequestEvent object that will be past as the event
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

