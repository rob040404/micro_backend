package com.social.SocialMicroservice.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.SocialMicroservice.client.UserServiceClient;
import com.social.SocialMicroservice.dto.AnswerFollowRequestsDTO;
import com.social.SocialMicroservice.dto.UsernameRequestDTO;
import com.social.SocialMicroservice.entities.*;
import com.social.SocialMicroservice.exceptions.*;
import com.social.SocialMicroservice.kafka.dto.FollowRequestEvent;
import com.social.SocialMicroservice.kafka.producer.FollowEventProducer;
import com.social.SocialMicroservice.repositories.FollowRequestRepository;
import com.social.SocialMicroservice.repositories.FollowersRepository;
import com.social.SocialMicroservice.repositories.OutboxEventRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@AllArgsConstructor @Getter @Log4j2
public class FollowersService {

    private final FollowRequestRepository followRequestRepository;
    private final UserServiceClient userServiceClient;
    private final FollowEventProducer followEventProducer;
    private final FollowersRepository followersRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final OutboxEventRepository outboxEventRepository;
    private final UsersCacheService usersCacheService;

    @Transactional
    public boolean followRequest(UsernameRequestDTO usernameRequestDTO, Authentication authentication){

        //Comprobar que no existe ya la solicitud (Por hacer)

        String followedUsername = usernameRequestDTO.getUsername();
        if (followedUsername==null || followedUsername.isBlank()){
            throw new RuntimeException("Invalid username");
        }

        CustomUserDetails details = (CustomUserDetails) authentication.getDetails();
        UUID followerId = details.getId();


        // We call the consumer through this service so we can use cached functionalities.
        UUID followedUserId = getUserIdByUsername(followedUsername);

        if (followerId.equals(followedUserId)) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }

        // Comprobar duplicados - usar constraint unique en BD + manejo de excepción
        boolean alreadyExists = followRequestRepository
                .existsByFollowerIdAndFollowedId(followerId, followedUserId);

        if (alreadyExists) {
            return false; // O lanzar excepción según tu lógica
        }

        FollowRequest followRequest = FollowRequest.builder()
                                .followerId(followerId)
                                .followedId(followedUserId)
                                .status(FollowStatus.PENDING)
                                .build();

        followRequestRepository.save(followRequest);


        // We save the entityFollowRequest into the OutboxEvent table so the event can be processed by OutboxProcessor
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setId(UUID.randomUUID());
        outboxEvent.setAggregateId(followRequest.getId());
        outboxEvent.setEventType("FOLLOW_REQUEST_CREATED");
        outboxEvent.setPayload(toJson(followRequest));
        outboxEvent.setStatus(EventStatus.PENDING);

        outboxEventRepository.save(outboxEvent);

        return true;
    }

    @Transactional
    public String answerRequest(AnswerFollowRequestsDTO answer, Authentication authentication){
        //Que la solicitud sea borrada tras ser aceptada

        CustomUserDetails details = (CustomUserDetails) authentication.getDetails();
        UUID followedId = details.getId();

        FollowRequest followRequest = followRequestRepository.getById(answer.getFollowRequestId());

        if(!followRequest.getFollowedId().equals(followedId)){
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

            Followers savedFollower = followersRepository.save(newFollower);

            // We save the entityFollowRequest into the OutboxEvent table so the event can be processed by OutboxProcessor
            OutboxEvent outboxEvent = new OutboxEvent();
            outboxEvent.setId(UUID.randomUUID());
            outboxEvent.setAggregateId(newFollower.getId());
            outboxEvent.setEventType("FOLLOW_ANSWERED");
            outboxEvent.setPayload(toJson(newFollower));
            outboxEvent.setStatus(EventStatus.PENDING);

            outboxEventRepository.save(outboxEvent);

        }else if (!answer.isAnswer()){
            followRequest.setStatus(FollowStatus.REJECTED);
            followRequestRepository.save(followRequest);
        }

        return "The follow request has been " + followRequest.getStatus();
    }

    private String toJson(Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Error serializando a JSON: " + e.getMessage(), e);
        }
    }

    /**
     * We cannot call directly the consumer of UsersMicroservice if we want to do the caching
     * @param username
     * @return
     */
    public UUID getUserIdByUsername(String username) {
        //We need the method below to ensure UUID or null is retuned, but never String
        String uuidStr = usersCacheService.getUserIdByUsernameAsString(username);
        return uuidStr != null ? UUID.fromString(uuidStr) : null;
    }
}
