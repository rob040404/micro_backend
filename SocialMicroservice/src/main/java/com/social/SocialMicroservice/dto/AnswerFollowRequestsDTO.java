package com.social.SocialMicroservice.dto;

import com.social.SocialMicroservice.entities.FollowStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@AllArgsConstructor @Getter @Setter
public class AnswerFollowRequestsDTO {

    private UUID followRequestId;
    private boolean answer;

}
