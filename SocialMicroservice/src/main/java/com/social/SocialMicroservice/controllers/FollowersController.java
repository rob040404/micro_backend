package com.social.SocialMicroservice.controllers;

import com.social.SocialMicroservice.dto.AnswerFollowRequestsDTO;
import com.social.SocialMicroservice.dto.UsernameRequestDTO;
import com.social.SocialMicroservice.entities.FollowStatus;
import com.social.SocialMicroservice.erroconfig.ApiError;
import com.social.SocialMicroservice.exceptions.FollowerAlreadyExistsException;
import com.social.SocialMicroservice.services.FollowersService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Follow request controller", description = "Controller that manages the follow petition from one user to another")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Follow request was created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = Boolean.class)
                    )
            ),
            @ApiResponse(responseCode = "400",
                    description = "Cannot follow yourself",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            ),
            @ApiResponse(responseCode = "400",
                    description = "Wrong username",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )
    })
    @PostMapping("/follow_request")
    public ResponseEntity<Boolean> followRequest(@RequestBody @Valid UsernameRequestDTO followedUsername, Authentication authentication){

        return ResponseEntity.status(HttpStatus.CREATED).body(followersService.followRequest(followedUsername, authentication));
    }

    @Operation(summary = "Answer to a follow request controller", description = "This controller is created for a user to answer to the follow request of another user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                description = "Successful response to a follow request",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = FollowStatus.class)
                )
            ),
            @ApiResponse(responseCode = "400",
                    description = "Follower already exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApiError.class)
                    )
            )

    })
    @PostMapping("/follow_answer")
    public ResponseEntity<FollowStatus> answerRequest(@RequestBody @Valid AnswerFollowRequestsDTO answer, Authentication authentication){

        return ResponseEntity.status(HttpStatus.OK).body(followersService.answerRequest(answer, authentication));
    }
}
