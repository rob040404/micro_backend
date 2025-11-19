package com.users.UsersMicroservice;

import com.users.UsersMicroservice.dto.ResponseUserDTO;
import com.users.UsersMicroservice.dto.UserDTOConverter;
import com.users.UsersMicroservice.dto.RequestUserRegisterDTO;
import com.users.UsersMicroservice.entities.UserEntity;
import com.users.UsersMicroservice.entities.UserRole;
import com.users.UsersMicroservice.exception.DataIntegrityException;
import com.users.UsersMicroservice.exception.NewUserWithDifferentPasswordsException;
import com.users.UsersMicroservice.repositories.BookListRepository;
import com.users.UsersMicroservice.repositories.StorageService;
import com.users.UsersMicroservice.repositories.UserEntityRepository;
import com.users.UsersMicroservice.repositories.UserListRepository;
import com.users.UsersMicroservice.service.UserEntityService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.verification.VerificationMode;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private StorageService storageService;

    @Mock
    private UserDTOConverter userDTOConverter;

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private UserListRepository userListRepository;

    @Mock
    private BookListRepository bookListRepository;

    @Spy  // Cambiamos de @InjectMocks a @Spy
    @InjectMocks
    private UserEntityService userEntityService;

    @BeforeEach
    void setUp() {
        // Inyectar manualmente el repositorio en BaseService
        ReflectionTestUtils.setField(userEntityService, "repositorio", userEntityRepository);


        // Simular el contexto HTTP para MvcUriComponentsBuilder
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setContextPath("");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        // Limpiar el contexto después de cada test
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void createsNewUser(){

        //User tipo RequestsUserRegisterDTO: formato que se recibe desde el cliente
        RequestUserRegisterDTO userRequestDTO = new RequestUserRegisterDTO();
        userRequestDTO.setUsername("ted");
        userRequestDTO.setFullname("Ted Lasso");
        userRequestDTO.setEmail("lasso@gmail.com");
        userRequestDTO.setPassword("lasso");
        userRequestDTO.setPassword2("lasso");
        userRequestDTO.setGender("man");
        userRequestDTO.setBirthday(LocalDate.ofYearDay(1977, 1));

        //Contraseña codificada simulada
        String encodedPassword = "encoded_password_123";

        //Los roles que tendrá
        Set<UserRole> roles = Set.of(UserRole.USER);

        //Mockeamos un MultipartFile para la inserción de la imagen
        MockMultipartFile file = new MockMultipartFile("file", "avatar.jpg", "image/jpeg", "fake image content".getBytes());

        // UserEntity que devolverá el repositorio después de guardar
        UserEntity savedUserEntity = UserEntity.builder()
                .id(UUID.randomUUID())  // El repositorio asigna un ID
                .username("ted")
                .fullname("Ted Lasso")
                .email("lasso@gmail.com")
                .password(encodedPassword)
                .gender("man")
                .birthday(LocalDate.ofYearDay(1977, 1))
                .roles(roles)
                .profileImage("http://localhost:8080/files/avatar.jpg")
                .build();

        // ResponseUserDTO que devolverá el converter
        ResponseUserDTO responseUserDTO = new ResponseUserDTO();
        responseUserDTO.setUsername("ted");
        responseUserDTO.setFullname("Ted Lasso");
        responseUserDTO.setEmail("lasso@gmail.com");
        responseUserDTO.setGender("man");
        responseUserDTO.setBirthday(LocalDate.ofYearDay(1977, 1));
        responseUserDTO.setProfileImage("http://localhost:8080/files/avatar.jpg");

        // MOCKS
        Mockito.when(storageService.store(file)).thenReturn("avatar.jpg");
        Mockito.when(passwordEncoder.encode(userRequestDTO.getPassword())).thenReturn(encodedPassword);

        // Mock del repositorio - devuelve la entidad con ID
        Mockito.when(userEntityRepository.save(any(UserEntity.class))).thenReturn(savedUserEntity);

        // Mock del converter - ESTO ES LO QUE FALTABA
        Mockito.when(userDTOConverter.convertUserEntityToGetUserDTO(any(UserEntity.class)))
                .thenReturn(responseUserDTO);

        // Act
        ResponseUserDTO result = userEntityService.createUser(userRequestDTO, file);

        // Assert
        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(userRequestDTO.getFullname(), result.getFullname());
        assertEquals(userRequestDTO.getUsername(), result.getUsername());
        assertEquals(userRequestDTO.getBirthday(), result.getBirthday());
        assertEquals(userRequestDTO.getGender(), result.getGender());
        assertEquals(userRequestDTO.getEmail(), result.getEmail());
        assertEquals("http://localhost:8080/files/avatar.jpg", result.getProfileImage());

        // Verificar que se guardó correctamente
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userEntityRepository).save(userCaptor.capture());

        UserEntity capturedUser = userCaptor.getValue();
        assertEquals(encodedPassword, capturedUser.getPassword());
        assertTrue(capturedUser.getRoles().contains(UserRole.USER));
        assertEquals(1, capturedUser.getRoles().size());

        // Verificar las interacciones
        verify(storageService).store(file);
        verify(userDTOConverter).convertUserEntityToGetUserDTO(any(UserEntity.class));

        //Es normal que ponga cosas como USER SAVED with id null. Ya que en tests, eso no está guardado en la BD
    }



//Hay que Mockear todo lo que tu método llama en el camino hasta el punto que falla.
    @Test
    void shouldThrowDataIntegrityExceptionWhenFileIsProvidedAndUserAlreadyExists() {
        // Given
        RequestUserRegisterDTO userRequestDTO = new RequestUserRegisterDTO();
        userRequestDTO.setUsername("ted");
        userRequestDTO.setFullname("Ted Lasso");
        userRequestDTO.setEmail("lasso@gmail.com");
        userRequestDTO.setPassword("lasso");
        userRequestDTO.setPassword2("lasso");
        userRequestDTO.setGender("man");
        userRequestDTO.setBirthday(LocalDate.ofYearDay(1977, 1));

        // Archivo NO vacío
        MockMultipartFile file = new MockMultipartFile("file", "avatar.jpg", "image/jpeg", "contenido".getBytes());

        //Los dos siguientes when: son pasos previos que tu método ejecuta sí o sí al hacer el save(), aunque estén fuera del try
        // Simular que la contraseña se codifica
        when(passwordEncoder.encode("lasso")).thenReturn("encoded_pass");

        // Simular que el almacenamiento de imagen funciona
        when(storageService.store(file)).thenReturn("avatar.jpg");

        // 🔥 Simular que save() lanza DataIntegrityViolationException
        when(userEntityRepository.save(any(UserEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Unique constraint violated"));

        // When + Then
        DataIntegrityException exception = assertThrows(
                DataIntegrityException.class,
                () -> userEntityService.createUser(userRequestDTO, file)
        );

        assertEquals("Failed to save User in data base: Username probably already exists", exception.getMessage());

        // Verificar que NO se intentó guardar más de una vez
        verify(userEntityRepository).save(any(UserEntity.class));
        // Verificar que sí se codificó la contraseña (el flujo llegó hasta ahí)
        verify(passwordEncoder).encode("lasso");
        // Verificar que se intentó subir la imagen (si tu lógica lo hace antes del save)
        verify(storageService).store(file);
    }


    @Test
    void shouldThrowDataIntegrityExceptionWhenFileIsEmptyAndUserAlreadyExists() {

        // Given
        RequestUserRegisterDTO userRequestDTO = new RequestUserRegisterDTO();
        userRequestDTO.setUsername("ted");
        userRequestDTO.setFullname("Ted Lasso");
        userRequestDTO.setEmail("lasso@gmail.com");
        userRequestDTO.setPassword("lasso");
        userRequestDTO.setPassword2("lasso");
        userRequestDTO.setGender("man");
        userRequestDTO.setBirthday(LocalDate.ofYearDay(1977, 1));

        // Archivo vacío
        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]); // o new byte[] {}

        when(passwordEncoder.encode("lasso")).thenReturn("encoded_pass");
        // ❌ NO mockeamos storageService.store(), porque no se llamará

        when(userEntityRepository.save(any()))
                .thenThrow(new DataIntegrityViolationException("..."));

        DataIntegrityException ex = assertThrows(DataIntegrityException.class, () ->
                userEntityService.createUser(userRequestDTO, emptyFile)
        );

        assertEquals("Failed to save User in data base: Username probably already exists", ex.getMessage());

        // ✅ Verificamos explícitamente que NO se llamó a store
        verify(storageService, never()).store(any());
    }

    @Test
    void shouldThrowNewUserWithDifferentPasswordsExceptionWhenPasswordsDontMatch(){
        // Given
        RequestUserRegisterDTO userRequestDTO = new RequestUserRegisterDTO();
        userRequestDTO.setUsername("ted");
        userRequestDTO.setFullname("Ted Lasso");
        userRequestDTO.setEmail("lasso@gmail.com");
        userRequestDTO.setPassword("lasso");
        userRequestDTO.setPassword2("lasso2"); //different
        userRequestDTO.setGender("man");
        userRequestDTO.setBirthday(LocalDate.ofYearDay(1977, 1));

        // Archivo NO vacío
        MockMultipartFile file = new MockMultipartFile("file", "avatar.jpg", "image/jpeg", "contenido".getBytes());


        NewUserWithDifferentPasswordsException ex = assertThrows(NewUserWithDifferentPasswordsException.class, ()->
                userEntityService.createUser(userRequestDTO, file));

        assertEquals("The passwords don't match", ex.getMessage());

        // Verificar que NO se llamó a nada (buena práctica para asegurar aislamiento), como no se hace el save() no se ejecutan
        verify(passwordEncoder, never()).encode(anyString());
        verify(storageService, never()).store(any());
        verify(userEntityRepository, never()).save(any());

    }


}
