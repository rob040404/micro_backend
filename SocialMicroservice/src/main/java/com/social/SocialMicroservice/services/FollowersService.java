package com.social.SocialMicroservice.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.social.SocialMicroservice.client.UserServiceClient;
import com.social.SocialMicroservice.dto.AnswerFollowRequestsDTO;
import com.social.SocialMicroservice.dto.UsernameRequestDTO;
import com.social.SocialMicroservice.entities.*;
import com.social.SocialMicroservice.exceptions.*;
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

import java.time.Instant;
import java.util.Optional;
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
    private final ObjectMapper objectMapper;

    /**
     * Method called by the controller when someone sends a follow request to another user
     * @param usernameRequestDTO The follower's and the followed information
     * @param authentication User's authentication, from where we extract his details
     * @return True or false.
     */
    @Transactional
    public boolean followRequest(UsernameRequestDTO usernameRequestDTO, Authentication authentication){

        String followedUsername = usernameRequestDTO.getUsername();
        if (followedUsername==null || followedUsername.isBlank()){
            throw new InvalidUsernameException("Invalid username");
        }

        CustomUserDetails details = (CustomUserDetails) authentication.getDetails();
        UUID followerId = details.getId();

        // We call the consumer through this service so we can use cached functionalities.
        UUID followedUserId = getUserIdByUsername(followedUsername);

        if (followerId.equals(followedUserId)) {
            throw new IllegalArgumentException("Cannot follow yourself");
        }

        // Check for duplicates - use unique constraint in database + exception handling
        boolean alreadyExists = followRequestRepository
                .existsByFollowerIdAndFollowedId(followerId, followedUserId);

        if (alreadyExists) {
            throw new FollowRequestAlreadyExistsExeption("This follow request already exists");
        }

        FollowRequest followRequest = FollowRequest.builder()
                                .followerId(followerId)
                                .followedId(followedUserId)
                                .status(FollowStatus.PENDING)
                                .build();

        FollowRequest savedFollowRequest =followRequestRepository.save(followRequest);

        // We save the entityFollowRequest into the OutboxEvent table so the event can be processed by OutboxProcessor
        OutboxEvent outboxEvent = new OutboxEvent();
        outboxEvent.setId(UUID.randomUUID());
        outboxEvent.setAggregateId(savedFollowRequest.getId());
        outboxEvent.setEventType("FOLLOW_REQUEST_CREATED");
        outboxEvent.setPayload(toJson(savedFollowRequest));
        outboxEvent.setStatus(EventStatus.PENDING);
        outboxEvent.setNextRetryAt(Instant.now());

        String payload = outboxEvent.getPayload();
        int payloadLength = payload.length();
        log.info("Payload length is {}", payloadLength);

        outboxEventRepository.save(outboxEvent);

        return true;
    }

    /**
     * Method called by the controller when someone answers a follow request from another user
     * @param answer It's a DTO with the follow request id and the answer true/false (accepted/rejected)
     * @param authentication User's authentication, from where we extract his details
     * @return A String with the result, if the follow request was accepted or rejected
     */
    @Transactional
    public FollowStatus answerRequest(AnswerFollowRequestsDTO answer, Authentication authentication){

        //We extract the logged user's details
        CustomUserDetails details = (CustomUserDetails) authentication.getDetails();
        UUID followedId = details.getId();

        //We extract the follow request
        FollowRequest followRequest = followRequestRepository.findById(answer.getFollowRequestId())
                .orElseThrow(FollowedIdsDontMatchException::new);

        //We make sure that the follow request's status is PENDING, not accepted or rejected
        if (followRequest.getStatus() != FollowStatus.PENDING) {
            throw new FollowRequestAlreadyProcessedException();
        }

        //We check if the follower already exists, in this case there is no need to answer. This should also be managed
        //in the frontend
        if(followersRepository.existsByFollowedIdAndFollowerId(followedId, followRequest.getFollowerId())){
            throw new FollowerAlreadyExistsException();
        }

        //If the answe provided is true (accepted)
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
            outboxEvent.setAggregateId(savedFollower.getId());
            outboxEvent.setEventType("FOLLOW_ANSWERED");
            outboxEvent.setPayload(toJson(savedFollower));
            outboxEvent.setStatus(EventStatus.PENDING);
            outboxEvent.setNextRetryAt(Instant.now());


            outboxEventRepository.save(outboxEvent);

        }else if (!answer.isAnswer()){ //If the user's answer id false (rejected)
            followRequest.setStatus(FollowStatus.REJECTED);
            followRequestRepository.save(followRequest);
        }
        return followRequest.getStatus();
    }

    /**
     * Method that transforms an object to json so can be stored in one column field of the database
     * In this case we serialize the kafka event object into json
     * @param obj Kafka DTO event object
     * @return String with json format for the object
     */
    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException("Error serializando a JSON: " + e.getMessage(), e);
        }
    }

    /**
     * We cannot call directly the consumer of UsersMicroservice if we want to do the caching
     * We need the method below to ensure UUID or null is retuned, but never String, because if the answer is cached in
     * Redis, it is returned as String, but we need it as a UUID. That's why we call this method to call
     * getUserIdByUsernameAsString(username), so we can get the answer as String (if cached) or UUID (if not cached)
     * and transform it into a UUID
     */
    public UUID getUserIdByUsername(String username) {
        String uuidStr = usersCacheService.getUserIdByUsernameAsString(username);
        if (uuidStr == null || uuidStr.isBlank()) {
            throw new UserNotFoundException();
        }
        try {
            return UUID.fromString(uuidStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalStateException("Invalid UUID format for this user ", e);
        }
    }
}
