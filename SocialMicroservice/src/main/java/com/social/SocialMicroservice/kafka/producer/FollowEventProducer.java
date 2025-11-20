package com.social.SocialMicroservice.kafka.producer;

import com.social.SocialMicroservice.entities.FollowRequest;
import com.social.SocialMicroservice.entities.Followers;
import com.social.SocialMicroservice.kafka.dto.FollowAnsweredEvent;
import com.social.SocialMicroservice.kafka.dto.FollowRequestEvent;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class FollowEventProducer {

    //The KafkaTemplate created in configuration to send messages to topics using the configured fabric
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${topic.follow-requests}")
    private String topic;

    @Value("${topic.follow-answers}")
    private String topic2;

    public FollowEventProducer(@Qualifier("kafkaTemplate")KafkaTemplate<String, Object> kafkaTemplate) {
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

        //kafkaTemplate.send(topic, event);
        kafkaTemplate.send(topic, event.followedId().toString(), event);
//                                ↑ key: user that receives the follow   value: event
    }

    public void publisFollowAnswerEvent(Followers fs){

        FollowAnsweredEvent event = new FollowAnsweredEvent(
                fs.getId(),
                fs.getFollowedId(),
                fs.getFollowerId(),
                fs.getFollowedSince()
        );

        kafkaTemplate.send(topic2, event.followerRegisterId().toString(), event);
    }
}

