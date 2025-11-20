package com.social.SocialMicroservice.controllers;

import com.social.SocialMicroservice.dto.AnswerFollowRequestsDTO;
import com.social.SocialMicroservice.dto.UsernameRequestDTO;
import com.social.SocialMicroservice.services.FollowersService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/social")
public class FollowersController {

    private final FollowersService followersService;

    @PostMapping("/follow_request")
    public ResponseEntity<?> followRequest(@RequestBody @Valid UsernameRequestDTO followedUsername, Authentication authentication){

        return ResponseEntity.status(HttpStatus.OK).body(followersService.followRequest(followedUsername, authentication));
    }

    @PostMapping("/follow_answer")
    public ResponseEntity<?> answerRequest(@RequestBody @Valid AnswerFollowRequestsDTO answer, Authentication authentication){

        return ResponseEntity.status(HttpStatus.CREATED).body(followersService.answerRequest(answer, authentication));
    }
}
