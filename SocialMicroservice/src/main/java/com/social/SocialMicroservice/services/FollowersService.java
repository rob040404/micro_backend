package com.social.SocialMicroservice.services;

import com.social.SocialMicroservice.client.UserServiceClient;
import com.social.SocialMicroservice.dto.AnswerFollowRequestsDTO;
import com.social.SocialMicroservice.dto.UsernameRequestDTO;
import com.social.SocialMicroservice.entities.FollowRequest;
import com.social.SocialMicroservice.entities.FollowStatus;
import com.social.SocialMicroservice.entities.Followers;
import com.social.SocialMicroservice.exceptions.*;
import com.social.SocialMicroservice.kafka.producer.FollowEventProducer;
import com.social.SocialMicroservice.repositories.FollowRequestRepository;
import com.social.SocialMicroservice.repositories.FollowersRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor @Getter
public class FollowersService {

    private final FollowRequestRepository followRequestRepository;
    private final UserServiceClient userServiceClient;
    private final FollowEventProducer followEventProducer;
    private final FollowersRepository followersRepository;

    public boolean followRequest(UsernameRequestDTO usernameRequestDTO, Authentication authentication){

        //Comprobar que no existe ya la solicitud
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

        followRequestRepository.save(followRequest);
        // Kafka event publication
        followEventProducer.publishFollowRequestEvent(followRequest);

        return true;
    }

    //Comprobar
    public String answerRequest(AnswerFollowRequestsDTO answer, Authentication authentication){
        //Que la solicitud sea borrada tras ser aceptada

        CustomUserDetails details = (CustomUserDetails) authentication.getDetails();
        UUID followedId = details.getId();

        FollowRequest followRequest = followRequestRepository.getById(answer.getFollowRequestId());

        if(followRequest.getFollowedId().equals(followedId)){
            throw new FollowedIdsDontMatchException(followRequest.getFollowedId() + " vs " + followedId);
        }

        if (followRequest.getStatus() != FollowStatus.PENDING) {
            throw new FollowRequestAlreadyProcessedException();
        }

        if(followersRepository.existsByFollowedIdAndFollowerId(followedId, followRequest.getFollowerId())){
            throw new FollowerAlreadyExistsException();
        }

        if (answer.isAnswer()){
            followRequest.setStatus(FollowStatus.ACCEPTED);
            followRequestRepository.save(followRequest);

            Followers newFollower = Followers.builder()
                    .followedId(followRequest.getFollowedId())
                    .followerId(followRequest.getFollowerId())
                    .build();

            followersRepository.save(newFollower);

        }else if (!answer.isAnswer()){
            followRequest.setStatus(FollowStatus.REJECTED);
        }

        return "The follow request has been " + followRequest.getStatus();
    }

}
