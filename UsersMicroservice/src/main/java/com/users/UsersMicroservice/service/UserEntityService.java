package com.users.UsersMicroservice.service;


import com.users.UsersMicroservice.controllers.FileController;
import com.users.UsersMicroservice.dto.*;
import com.users.UsersMicroservice.entities.BookList;
import com.users.UsersMicroservice.entities.UserEntity;
import com.users.UsersMicroservice.entities.UserList;
import com.users.UsersMicroservice.entities.UserRole;
import com.users.UsersMicroservice.exception.*;
import com.users.UsersMicroservice.repositories.BookListRepository;
import com.users.UsersMicroservice.repositories.StorageService;
import com.users.UsersMicroservice.repositories.UserEntityRepository;
import com.users.UsersMicroservice.repositories.UserListRepository;
import com.users.UsersMicroservice.security.PasswordEncoderConfig;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Este servicio extiende BaseService. Usará sus métodos
 */
@Log4j2
@Service 
@AllArgsConstructor
public class UserEntityService extends BaseService<UserEntity, UUID, UserEntityRepository>{

    private PasswordEncoderConfig passwordEncoderConfig;
    private PasswordEncoder passwordEncoder;
    private StorageService storageService;
    private UserDTOConverter userDTOConverter;
    private UserEntityRepository userEntityRepository;
    private UserListRepository userListRepository;
    private BookListRepository bookListRepository;
    private final CacheService cacheService;

    /**
     * Method for user creation that is called from the controller
     * @param newUser UserRegistrationRequestDTO with the new user's information
     * @param file profile picture
     * @return UserRegistrationResponseDTO with some no sensible data that is returned to the frontend
     */
    public UserRegistrationResponseDTO createUser(UserRegistrationRequestDTO newUser, MultipartFile file) {

        log.trace("UserEntityService - createUser - Accessing method");
        String urlImage = null;
        UserEntity savedUser;

        if (Objects.equals(newUser.getPassword(), newUser.getPassword2())) {    //Both passwords should be equal

            if(!file.isEmpty()) {
                String image = storageService.store(file); //It returns the name of the stored file name
                urlImage = MvcUriComponentsBuilder            //We construct the uri we will use for the database
                        .fromMethodName(FileController.class, "serveFile", image, null) //It takes the information from method serveFile of FileController
                        .build().toString();								//We construct all the uri path of the file to String
            }

            //We build the UserEntity with builder
            UserEntity userEntity = UserEntity.builder()
                    .username(newUser.getUsername())
                    .password(passwordEncoder.encode(newUser.getPassword()))
                    .profileImage(urlImage)
                    .fullname(newUser.getFullname())
                    .email(newUser.getEmail())
                    .birthday(newUser.getBirthday())
                    .gender(newUser.getGender())
                    .roles(Set.of(UserRole.USER))
                    .build();

            try {
                savedUser =  save(userEntity);
                log.info("USER SAVED with id {}", savedUser.getId());
                return userDTOConverter.convertUserEntityToGetUserDTO(savedUser);

            } catch (DataIntegrityViolationException ex) {
                log.warn("Data Integrity Violation Exception: Username or email probably already exists or something else failed, but user was not saved ");
                throw new DataIntegrityException("Invalid Input Data");
            }

        } else {
            log.warn("User's passwords are different");
            throw new NewUserWithDifferentPasswordsException();
        }
    }

    /**
     * Method that is called from the Login controller that is called from the SecurityMicroservice.
     * Checks if user exists delivers the information to the Security microservice
     * @param email Receives the email sent by the Security microservice so the user could be found
     * @return UserLoginResponseDTO, delivers the information to the Security microservice
     */
    public UserLoginResponseDTO userLogin(String email){

        UserEntity user = userEntityRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException());
        log.trace("Entering userLogin");
        log.trace("User id: {}", user.getId());
        return new UserLoginResponseDTO(user.getId(), user.getUsername(), user.getFullname(), user.getEmail(), user.getPassword(), user.getProfileImage(), user.getRoles());
    }

    /**
     * Called from the sendUserId controller which can be called from other microservices that need the user's id
     * @param username The username must be sent as a param in this case.
     * @return User's id
     */
    public UUID sendUserId(String username){
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username must not be null or empty");
        }

        String uuidStr = cacheService.sendUserIdByUsernameAsString(username);
        if (uuidStr == null) {
            throw new UserNotFoundException();
        }
        return UUID.fromString(uuidStr);
    }

    /**
     * Called from the createList controller. It creates a new list for the user, like Favorites or Must Read
     * @param newList Name of the list thar should be created
     * @param authentication The authentication object to extract the user's details
     * @return The name of the created list
     */
    public String createList(CreateListRequestDTO newList, Authentication authentication){

        String email= (String) authentication.getPrincipal();
        UserEntity user = userEntityRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        UserList list = new UserList();
        list.setUser(user);
        list.setListName(newList.getListName());

        UserList savedList = userListRepository.save(list);
        log.info("New list saved for user {}", user.getId());

        return savedList.getListName();

    }

    /**
     * Called from the addBook controller, so the user can add a book to one of his/her lists
     * @param newBook The book that should be saved.
     * @param authentication The authentication object to extract the user's details
     * @return String with the information of the saved book and the list it fas saved into
     */
    @Transactional
    public String saveBookToList(SaveBookRequestDTO newBook, Authentication authentication){

        String email= (String) authentication.getPrincipal();
        UserEntity user = userEntityRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);

        for(UUID list: newBook.getUserListId()){

            // We verify that the list belongs to the user
            UserList userList = userListRepository.findById(list)
                    .orElseThrow(()-> new UserListNotFoundException(" List was not found"));

            if (!userList.getUser().getId().equals(user.getId())) {
                throw new AccessDeniedException("You can only add books to your own lists");
            }

            BookList book = new BookList();
            book.setBookId(newBook.getBookId());
            book.setList(userList);
            BookList savedBook =bookListRepository.save(book);

            log.info("Book {} saved in list {} for user {}",
                    savedBook.getBookId(), savedBook.getList(), user.getId());
        }

        return "Book with id has been saved in list " ;
    }

    /*
    public void getUsersListsWithBook(Authentication authentication, Long bookID){

        // Valdation of bookId Do it with
        if (bookID == null) {
            throw new IllegalArgumentException("Book ID cannot be null");
        }

        // Validar autenticación
        if (authentication == null || !(authentication.getDetails() instanceof CustomUserDetails)) {
            throw new AccessDeniedException("User not authenticated or invalid details");
        }

        CustomUserDetails details = (CustomUserDetails) authentication.getDetails();
        UUID userID = details.getId();

        if (userID == null) {
            throw new IllegalStateException("User ID cannot be null");
        }

    }
    
     */
}
