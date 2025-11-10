package com.security.security.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class AuthResponseDTO {

    //respuesta del autenticador, que es el token jenerado con generateToken()
    private String fullname;
    private String username;
    private String profileImage;
    private String token;

}
