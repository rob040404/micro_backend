package com.users.UsersMicroservice;

import com.users.UsersMicroservice.dto.UserRegistrationResponseDTO;
import com.users.UsersMicroservice.dto.UserDTOConverter;
import com.users.UsersMicroservice.dto.UserRegistrationRequestDTO;
import com.users.UsersMicroservice.entities.UserEntity;
import com.users.UsersMicroservice.entities.UserRole;
import com.users.UsersMicroservice.exception.DataIntegrityException;
import com.users.UsersMicroservice.exception.NewUserWithDifferentPasswordsException;
import com.users.UsersMicroservice.repositories.BookListRepository;
import com.users.UsersMicroservice.repositories.StorageService;
import com.users.UsersMicroservice.repositories.UserEntityRepository;
import com.users.UsersMicroservice.repositories.UserListRepository;
import com.users.UsersMicroservice.service.S3StorageService;
import com.users.UsersMicroservice.service.UserEntityService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unitary tests for the services
 */
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private S3StorageService s3StorageService;

    @Mock
    private UserDTOConverter userDTOConverter;

    @Mock
    private UserEntityRepository userEntityRepository;

    @Spy  // We switch from @InjectMocks to @Spy
    @InjectMocks
    private UserEntityService userEntityService;

    @BeforeEach
    void setUp() {
        // We manually inject the repository into BaseService
        ReflectionTestUtils.setField(userEntityService, "repositorio", userEntityRepository);


        // Simulate the HTTP context for MvcUriComponentsBuilder
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setScheme("http");
        request.setServerName("localhost");
        request.setServerPort(8080);
        request.setContextPath("");
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @AfterEach
    void tearDown() {
        // We clean the context after every test
        RequestContextHolder.resetRequestAttributes();
    }

    /**
     * Test for creating a new user
     */
    @Test
    void createsNewUser(){

        //User tipo RequestsUserRegisterDTO: formato que se recibe desde el cliente
        UserRegistrationRequestDTO userRequestDTO = new UserRegistrationRequestDTO();
        userRequestDTO.setUsername("ted");
        userRequestDTO.setFullname("Ted Lasso");
        userRequestDTO.setEmail("lasso@gmail.com");
        userRequestDTO.setPassword("lasso");
        userRequestDTO.setPassword2("lasso");
        userRequestDTO.setGender("man");
        userRequestDTO.setBirthday(LocalDate.ofYearDay(1977, 1));

        //Simulated and codified password
        String encodedPassword = "encoded_password_123";

        //The roles the user will have
        Set<UserRole> roles = Set.of(UserRole.USER);

        //We mock a MultipartFile for image insertion
        MockMultipartFile file = new MockMultipartFile("file", "avatar.jpg", "image/jpeg", "fake image content".getBytes());

        // UserEntity that the repository will return after saving
        UserEntity savedUserEntity = UserEntity.builder()
                .id(UUID.randomUUID())  // The repository assigns authomaticallt an ID
                .username("ted")
                .fullname("Ted Lasso")
                .email("lasso@gmail.com")
                .password(encodedPassword)
                .gender("man")
                .birthday(LocalDate.ofYearDay(1977, 1))
                .roles(roles)
                .profileImage("http://localhost:8080/files/avatar.jpg")
                .build();

        // ResponseUserDTO that the converter will build
        UserRegistrationResponseDTO userRegistrationResponseDTO = new UserRegistrationResponseDTO();
        userRegistrationResponseDTO.setUsername("ted");
        userRegistrationResponseDTO.setFullname("Ted Lasso");
        userRegistrationResponseDTO.setGender("man");
        userRegistrationResponseDTO.setBirthday(LocalDate.ofYearDay(1977, 1));
        userRegistrationResponseDTO.setProfileImage("http://localhost:8080/files/avatar.jpg");

        // MOCKS
        Mockito.when(s3StorageService.store(file)).thenReturn("avatar.jpg");
        Mockito.when(passwordEncoder.encode(userRequestDTO.getPassword())).thenReturn(encodedPassword);

        // Mock of the repository - it returns the entity with the id
        Mockito.when(userEntityRepository.save(any(UserEntity.class))).thenReturn(savedUserEntity);

        // Mock of the converter
        Mockito.when(userDTOConverter.convertUserEntityToGetUserDTO(any(UserEntity.class)))
                .thenReturn(userRegistrationResponseDTO);

        // Act
        UserRegistrationResponseDTO result = userEntityService.createUser(userRequestDTO, file);

        // Assert
        assertNotNull(result, "El resultado no debería ser null");
        assertEquals(userRequestDTO.getFullname(), result.getFullname());
        assertEquals(userRequestDTO.getUsername(), result.getUsername());
        assertEquals(userRequestDTO.getBirthday(), result.getBirthday());
        assertEquals(userRequestDTO.getGender(), result.getGender());
        assertEquals("http://localhost:8080/files/avatar.jpg", result.getProfileImage());

        // Verifying if it was saved correctly
        ArgumentCaptor<UserEntity> userCaptor = ArgumentCaptor.forClass(UserEntity.class);
        verify(userEntityRepository).save(userCaptor.capture());

        UserEntity capturedUser = userCaptor.getValue();
        assertEquals(encodedPassword, capturedUser.getPassword());
        assertTrue(capturedUser.getRoles().contains(UserRole.USER));
        assertEquals(1, capturedUser.getRoles().size());

        // Verifying the interactions
        verify(s3StorageService).store(file);
        verify(userDTOConverter).convertUserEntityToGetUserDTO(any(UserEntity.class));

        //It is normal to see in console USER SAVED with id null. This is a test, it is not saved in a DB
    }


    /**
     * Testing exceptions when user already exists and file is provided
     */
    @Test
    void shouldThrowDataIntegrityExceptionWhenFileIsProvidedAndUserAlreadyExists() {
        // Given
        UserRegistrationRequestDTO userRequestDTO = new UserRegistrationRequestDTO();
        userRequestDTO.setUsername("ted");
        userRequestDTO.setFullname("Ted Lasso");
        userRequestDTO.setEmail("lasso@gmail.com");
        userRequestDTO.setPassword("lasso");
        userRequestDTO.setPassword2("lasso");
        userRequestDTO.setGender("man");
        userRequestDTO.setBirthday(LocalDate.ofYearDay(1977, 1));

        // Not empty file
        MockMultipartFile file = new MockMultipartFile("file", "avatar.jpg", "image/jpeg", "contenido".getBytes());


        //The following two when: they are previous steps in our method that are executed when save() is done.

        // Password simulation
        when(passwordEncoder.encode("lasso")).thenReturn("encoded_pass");

        // Simulating that the file storage in working
        when(s3StorageService.store(file)).thenReturn("avatar.jpg");

        // Simulating that save() throws DataIntegrityViolationException
        when(userEntityRepository.save(any(UserEntity.class)))
                .thenThrow(new DataIntegrityViolationException("Unique constraint violated"));

        // When + Then
        DataIntegrityException exception = assertThrows(
                DataIntegrityException.class,
                () -> userEntityService.createUser(userRequestDTO, file)
        );

        assertEquals("Failed to save User in data base: Invalid Input Data", exception.getMessage());

        // Verifying that save() was not executed more than one time
        verify(userEntityRepository).save(any(UserEntity.class));
        // Verifying that the password was encoded
        verify(passwordEncoder).encode("lasso");
        // Verifying that the image was uploaded (at least it was attempted)
        verify(s3StorageService).store(file);
    }


    /**
     * Testing exceptions when user already exists and file is not provided
     */
    @Test
    void shouldThrowDataIntegrityExceptionWhenFileIsEmptyAndUserAlreadyExists() {

        // Given
        UserRegistrationRequestDTO userRequestDTO = new UserRegistrationRequestDTO();
        userRequestDTO.setUsername("ted");
        userRequestDTO.setFullname("Ted Lasso");
        userRequestDTO.setEmail("lasso@gmail.com");
        userRequestDTO.setPassword("lasso");
        userRequestDTO.setPassword2("lasso");
        userRequestDTO.setGender("man");
        userRequestDTO.setBirthday(LocalDate.ofYearDay(1977, 1));

        // Empty file
        MockMultipartFile emptyFile = new MockMultipartFile("file", new byte[0]); // o new byte[] {}

        when(passwordEncoder.encode("lasso")).thenReturn("encoded_pass");

        when(userEntityRepository.save(any()))
                .thenThrow(new DataIntegrityViolationException("..."));

        DataIntegrityException ex = assertThrows(DataIntegrityException.class, () ->
                userEntityService.createUser(userRequestDTO, emptyFile)
        );

        assertEquals("Failed to save User in data base: Invalid Input Data", ex.getMessage());

        // Verifying that store was not called
        verify(s3StorageService, never()).store(any());
    }

    /**
     * Testing the exception thrown when passwords don't match
     */
    @Test
    void shouldThrowNewUserWithDifferentPasswordsExceptionWhenPasswordsDontMatch(){
        // Given
        UserRegistrationRequestDTO userRequestDTO = new UserRegistrationRequestDTO();
        userRequestDTO.setUsername("ted");
        userRequestDTO.setFullname("Ted Lasso");
        userRequestDTO.setEmail("lasso@gmail.com");
        userRequestDTO.setPassword("lasso");
        userRequestDTO.setPassword2("lasso2"); //different
        userRequestDTO.setGender("man");
        userRequestDTO.setBirthday(LocalDate.ofYearDay(1977, 1));


        MockMultipartFile file = new MockMultipartFile("file", "avatar.jpg", "image/jpeg", "contenido".getBytes());

        NewUserWithDifferentPasswordsException ex = assertThrows(NewUserWithDifferentPasswordsException.class, ()->
                userEntityService.createUser(userRequestDTO, file));

        assertEquals("The passwords don't match", ex.getMessage());

        verify(passwordEncoder, never()).encode(anyString());
        verify(s3StorageService, never()).store(any());
        verify(userEntityRepository, never()).save(any());

    }


}
