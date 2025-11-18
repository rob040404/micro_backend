package com.users.UsersMicroservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.users.UsersMicroservice.controllers.UserController;
import com.users.UsersMicroservice.dto.RequestUserRegisterDTO;
import com.users.UsersMicroservice.dto.ResponseUserDTO;
import com.users.UsersMicroservice.security.ApiFilter;
import com.users.UsersMicroservice.security.JWTUtil;
import com.users.UsersMicroservice.service.UserEntityService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc(addFilters = false) //Desactiva la seguridad, que en los tests se aplica aunque sea ruta publica

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserEntityService userEntityService;

    @MockitoBean
    private JWTUtil jwtUtil;

    @MockitoBean
    private ApiFilter apiFilter;

    @Autowired
    private ObjectMapper objectMapper;


    @Test
    void shouldRegisterUserSuccessfully() throws Exception {

        // 1. Crear el DTO de la petición (el JSON)
        RequestUserRegisterDTO requestDto = RequestUserRegisterDTO.builder()
                .username("carlos")
                .email("carlos@example.com")
                .password("12345")
                .password2("12345")
                .fullname("Carlos Ruiz")
                .gender("male")
                .birthday(LocalDate.of(1997, 5, 20))
                .build();

        // 2. Convertir JSON a String para enviarlo en el multipart
        String json = objectMapper.writeValueAsString(requestDto);

        // 3. Crear archivo simulado
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                MediaType.IMAGE_PNG_VALUE,
                "fakeimagecontent".getBytes()
        );

        // 4. Crear respuesta simulada del servicio
        ResponseUserDTO responseDto = ResponseUserDTO.builder()
                .email("carlos@example.com")
                .fullname("Carlos Ruiz")
                .username("carlos")
                .gender("male")
                .birthday(LocalDate.of(1997, 5, 20))
                .roles(Set.of("ROLE_USER"))
                .profileImage("avatar.png")
                .build();

        when(userEntityService.createUser(any(), any())).thenReturn(responseDto);

        // 5. Realizar la llamada
        mockMvc.perform(
                        multipart("/user/register")
                                .file(file)
                                .file(new MockMultipartFile(
                                        "new",
                                        "",
                                        MediaType.APPLICATION_JSON_VALUE,
                                        json.getBytes()
                                ))
                                .contentType(MediaType.MULTIPART_FORM_DATA)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("carlos"))
                .andExpect(jsonPath("$.fullname").value("Carlos Ruiz"))
                .andExpect(jsonPath("$.roles[0]").value("ROLE_USER"));

        // 6. Verificar que el servicio fue llamado
        verify(userEntityService, times(1)).createUser(any(), any());
    }


}
