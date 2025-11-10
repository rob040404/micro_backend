package com.security.security.service;

import com.security.security.client.UserServiceClient;
import com.security.security.dto.AuthRequestDTO;
import com.security.security.dto.AuthResponseDTO;
import com.security.security.entity.User;
import com.security.security.entity.UserRole;

import com.security.security.security.JWTUtil;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

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


    /*
    public boolean authenticate(String email, String password) {
        return userRepository.findByEmail(email)
                .map(user -> encodedPassword.matches(password, user.getPassword()))
                .orElse(false);
    }
*/


    //El .map() de Optional transforma el contenido si existe. (Hacerlo con todos los códigos que hemos hecho sin saberlo)
    //Es .matches() quien dice si es true o false

    public AuthResponseDTO login(AuthRequestDTO authRequestDTO){

        if(authRequestDTO.getEmail() != null && authRequestDTO.getPassword()!=null){
            User user = userServiceClient.getUser(authRequestDTO.getEmail());

            if (encodedPassword.matches(authRequestDTO.getPassword(), user.getPassword())){
                // 2. Extraer roles como lista de strings
                List<String> roles = user.getRoles().stream()
                        .map(UserRole::name) // UserRole.ADMIN → "ADMIN"
                        .collect(Collectors.toList());

                String token = jwtUtil.generateToken(authRequestDTO.getEmail(), user.getId(), roles, user.getUsername());

                return new AuthResponseDTO(user.getFullname(), user.getUsername(), user.getProfileImage(),token);
            }

        }
        log.warn("Bad credentials when trying in method login for {}", authRequestDTO.getEmail());
        throw new BadCredentialsException("Credenciales inválidas");
    }


}


