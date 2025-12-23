package com.users.UsersMicroservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.users.UsersMicroservice.controllers.UserController;
import com.users.UsersMicroservice.dto.UserRegistrationRequestDTO;
import com.users.UsersMicroservice.dto.UserRegistrationResponseDTO;
import com.users.UsersMicroservice.security.ApiFilter;
import com.users.UsersMicroservice.security.JWTUtil;
import com.users.UsersMicroservice.service.UserEntityService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Controller Tests
 */
@AutoConfigureMockMvc(addFilters = false) //Disable security, which is applied in tests even if it's a public route.
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

        // 1. Create the request DTO (the JSON)
        UserRegistrationRequestDTO requestDto = UserRegistrationRequestDTO.builder()
                .username("carlos")
                .email("carlos@example.com")
                .password("12345")
                .password2("12345")
                .fullname("Carlos Ruiz")
                .gender("male")
                .birthday(LocalDate.of(1997, 5, 20))
                .build();

        // 2. Convert JSON to String to send it in the multipart
        String json = objectMapper.writeValueAsString(requestDto);

        // 3. Create a simulated file
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "avatar.png",
                MediaType.IMAGE_PNG_VALUE,
                "fakeimagecontent".getBytes()
        );

        // 4. Create a simulated response os the service
        UserRegistrationResponseDTO responseDto = UserRegistrationResponseDTO.builder()
                .fullname("Carlos Ruiz")
                .username("carlos")
                .gender("male")
                .birthday(LocalDate.of(1997, 5, 20))
                .profileImage("avatar.png")
                .build();

        when(userEntityService.createUser(any(), any())).thenReturn(responseDto);

        // 5. Making a call
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
                .andExpect(jsonPath("$.fullname").value("Carlos Ruiz"));

        // 6. Verifying the service was called
        verify(userEntityService, times(1)).createUser(any(), any());
    }


}
