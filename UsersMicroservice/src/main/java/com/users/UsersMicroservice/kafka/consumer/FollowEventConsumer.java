package com.users.UsersMicroservice.kafka.consumer;

import com.users.UsersMicroservice.kafka.dto.FollowRequestEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public class FollowEventConsumer {

    @KafkaListener(topics = "${topic.follow-requests}", groupId = "notifications-group")
    public void onFollowRequest(FollowRequestEvent event) {  // ✅ Ahora recibe el objeto tipado
        System.out.println("Follow request from " + event.followerId() + " to " + event.followedId());
        System.out.println("Status: " + event.status());
        System.out.println("Created at: " + event.createdAt());

        // Aquí puedes procesar la notificación
    }
}
