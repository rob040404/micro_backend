package com.social.SocialMicroservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

/**
 * DTO for answer to a follow request
 */
@AllArgsConstructor @Getter @Setter
public class AnswerFollowRequestsDTO {

    private UUID followRequestId;
    private boolean answer;

}
