package com.book.BookMicroservice.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RequestUserDTO {

    /**
     * Clase para solicitar el user, para poder hacer los ratings
     * Ver bien qué necesito
     */
    private String userId;



}
