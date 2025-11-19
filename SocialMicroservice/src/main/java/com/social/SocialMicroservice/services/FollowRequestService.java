package com.social.SocialMicroservice.services;

import com.social.SocialMicroservice.client.UserServiceClient;
import com.social.SocialMicroservice.dto.UsernameRequestDTO;
import com.social.SocialMicroservice.entities.FollowRequest;
import com.social.SocialMicroservice.entities.FollowStatus;
import com.social.SocialMicroservice.exceptions.FollowRequestNotSavedException;
import com.social.SocialMicroservice.kafka.producer.FollowEventProducer;
import com.social.SocialMicroservice.repositories.FollowRequestRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor @Getter
public class FollowRequestService {

    private final FollowRequestRepository followRequestRepository;
    private final UserServiceClient userServiceClient;
    private final FollowEventProducer followEventProducer;

    public boolean followRequest(UsernameRequestDTO usernameRequestDTO, Authentication authentication){

        String followedUsername = usernameRequestDTO.getUsername();

        if (followedUsername==null || followedUsername.isBlank()){
            throw new RuntimeException("Invalid username");
        }

        CustomUserDetails details = (CustomUserDetails) authentication.getDetails();
        UUID followerId = details.getId();

        UUID followedUserId = userServiceClient.getUserId(followedUsername);

        FollowRequest followRequest = FollowRequest.builder()
                                .followerId(followerId)
                                .followedId(followedUserId)
                                .status(FollowStatus.PENDING)
                                .build();

        try {
            followRequestRepository.save(followRequest);
            // 2. Kafka event publication
            followEventProducer.publishFollowRequestEvent(followRequest);
        } catch (Exception e) {
            throw new FollowRequestNotSavedException();
        }
        return true;
    }
}
