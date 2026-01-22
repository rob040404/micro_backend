package com.users.UsersMicroservice.kafka.consumer;

import com.users.UsersMicroservice.kafka.dto.FollowAnsweredEventRequestDTO;
import com.users.UsersMicroservice.kafka.dto.FollowRequestEventRequestDTO;
import lombok.extern.log4j.Log4j2;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Kafka consumer class. Receives the messages sent fron producers of other microservices
 * Still in development
 */
@Service
@Log4j2
public class FollowEventConsumer {

    /**
     * Method that detects follow request messages
     * @param event
     */
    //@KafkaListener(topics = "${topic.follow-requests}", groupId = "${group.users}")
    public void onFollowRequest(FollowRequestEventRequestDTO event) {
        log.info("Follow request: followerId={}, followedId={}, status={}, createdAt={}",
                event.followerId(), event.followedId(), event.status(), event.createdAt());
        //In development: here we must call a service to process the business logic.
        //followService.processFollowRequest(event);
    }

    /**
     * A method thar detects answers to follow request messages
     * @param event
     */
    //@KafkaListener(topics = "${topic.follow-answers}", groupId = "${group.users}")
    public void onFollowAnswer(FollowAnsweredEventRequestDTO event){
        log.info("{} has accepted {} since {}", event.followedId(), event.followerId(), event.followedSince());
        //followService.processAnswerRequest(event);
    }
}
