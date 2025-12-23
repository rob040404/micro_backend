package com.security.security.service;

import com.security.security.client.UserServiceClient;
import com.security.security.dto.AuthRequestDTO;
import com.security.security.dto.AuthResponseDTO;
import com.security.security.entity.User;
import com.security.security.entity.UserRole;

import com.security.security.exception.WrongCredentialsException;
import com.security.security.security.JWTUtil;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class where the business login hapens
 */
@Log4j2
@Slf4j
@Service
public class UserService {

    private final JWTUtil jwtUtil;

    private final UserServiceClient userServiceClient;

    private final PasswordEncoder encodedPassword = new BCryptPasswordEncoder();

    public UserService(JWTUtil jwtUtil, UserServiceClient userServiceClient) {
        this.jwtUtil = jwtUtil;
        this.userServiceClient = userServiceClient;
    }

    /**
     * Method that is used by the login controller for business logic.
     * It receives the request DTO, gets the user data sending a request to the Users Microservice and if the user exists
     * and the passwords are right it generates the JWT token
     * @param authRequestDTO We get the email and password of the request
     * @return We return a response DTO with some of the user's data and JWT token
     */
    public AuthResponseDTO login(AuthRequestDTO authRequestDTO){

        if(authRequestDTO.getEmail() != null && authRequestDTO.getPassword()!=null){
            User user = userServiceClient.getUser(authRequestDTO.getEmail());

            if (encodedPassword.matches(authRequestDTO.getPassword(), user.getPassword())){
                // Extracts the roles as a List
                List<String> roles = user.getRoles().stream()
                        .map(UserRole::name) // UserRole.ADMIN → "ADMIN"
                        .collect(Collectors.toList());

                String token = jwtUtil.generateToken(authRequestDTO.getEmail(), user.getId(), roles, user.getUsername());

                return new AuthResponseDTO(user.getFullname(), user.getUsername(), user.getProfileImage(),token);
            }

        }
        log.warn("Bad credentials when trying in method login for {}", authRequestDTO.getEmail());
        throw new WrongCredentialsException();
    }


}


