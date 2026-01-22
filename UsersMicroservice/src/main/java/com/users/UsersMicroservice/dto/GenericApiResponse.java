package com.users.UsersMicroservice.dto;

import lombok.*;
import org.springframework.web.bind.annotation.RequestBody;

@AllArgsConstructor @NoArgsConstructor @Getter @Setter
public class GenericApiResponse {

    private String message;
}
