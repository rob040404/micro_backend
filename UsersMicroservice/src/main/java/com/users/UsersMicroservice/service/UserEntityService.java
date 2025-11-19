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
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

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

	/**
	 * Nos permite buscar un usuario por su nombre de usuario. LLama al método de BaseService
	 * @param username
	 * @return
	 */
	public Optional<UserEntity> findUserByUsername(String username) {
		return this.repositorio.findByUsername(username);
	}
	
	public Optional<UserEntity> findUserByEmail(String email){
		return this.repositorio.findByEmail(email);
	}



    public ResponseUserDTO createUser(RequestUserRegisterDTO newUser, MultipartFile file) {

        log.trace("UserEntityService - createUser - Entrando en método");
        String urlImage = null;
        UserEntity savedUser;

        if (newUser.getPassword().contentEquals(newUser.getPassword2())) {    //Si coinciden contraseña 1 con contraseña 2 (de confirmación)

            if(!file.isEmpty()) {
                log.trace("UserEntityService - createUser - Entrando en if ...");
                String image = storageService.store(file); //It returns the name of the stored file name
                urlImage = MvcUriComponentsBuilder            //Construimos la uri completa que vamos a almacenar en la bd
                        .fromMethodName(FileController.class, "serveFile", image, null) //Coge la info del metodo serveFile del FileController
                        .build().toString();								//We construct all the uri path of the file to String
            }

            //Construimos el objeto userEntity simplemente con el builder
            UserEntity userEntity = UserEntity.builder()
                    .username(newUser.getUsername())
                    .password(passwordEncoder.encode(newUser.getPassword())) //Revisar esto si no funciona, está cambiado del original
                    .profileImage(urlImage)
                    .fullname(newUser.getFullname())
                    .email(newUser.getEmail())
                    .birthday(newUser.getBirthday())
                    .gender(newUser.getGender())
                    .roles(Set.of(UserRole.USER))
                    .build();
            //En Java 9 podría ser Set.of(UserRole.USER)
            try { //Ver qué ocurre si se trata de register un usuario con email o username existente
                savedUser =  save(userEntity); //Metodo extendido de BaseService que tiene métodos Jpa
                log.info("USER SAVED with id {}", userEntity.getId());
                return userDTOConverter.convertUserEntityToGetUserDTO(savedUser);

            } catch (DataIntegrityViolationException ex) { //Capturamos si se viola la integridad (si se repite el username o email)
                log.warn("Data Integrity Violation Exception");
                throw new DataIntegrityException("Username probably already exists");
            }

        } else {    //Si no coinciden las contraseñas, lanzamos excepción
            log.warn("User's passwords are different");
            throw new NewUserWithDifferentPasswordsException();
        }
    }

    //Prueba de llamadas ente apis, no se usa
    public ResponseUserDTO sendUser(UUID id){

        UserEntity user = userEntityRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException(" "));

        return userDTOConverter.convertUserEntityToGetUserDTO(user);
    }

    public UserLoginDTO userLogin(String email){

        UserEntity user = userEntityRepository.getByEmail(email).orElseThrow(()-> new UsernameNotFoundException(email));
        log.trace("Entrando en userLogin");
        log.trace("Id del usuario: {}", user.getId());
        UserLoginDTO userLoginDTO= new UserLoginDTO(user.getId(), user.getUsername(), user.getFullname(), user.getEmail(), user.getPassword(), user.getProfileImage(), user.getRoles());
        return userLoginDTO;
    }

    public UUID sendUserId(String username){
        UserEntity user = userEntityRepository.findByUsername(username).
                orElseThrow(()-> new UserNotFoundException("Did not find user with username " + username));
        log.trace("User id: {}", user.getId());
        return user.getId();
    }


    public String createList(CreateListRequestDTO newList, Authentication authentication){

        if (newList.getListName()!=null){

            UUID userId;
            CustomUserDetails details = (CustomUserDetails) authentication.getDetails();

            if (details!=null){
                userId = details.getId();
                UserEntity user = userEntityRepository.findById(userId).orElseThrow(()-> new UserNotFoundException(" "));
                UserList list  = UserList.builder().user(user).listName(newList.getListName()).build();
                UserList savedList = userListRepository.save(list);

                if (savedList.getIdUserList() != null) {
                    log.info("New SAVED LIST for user {}", user.getId());
                    return savedList.getListName();
                } else {
                    log.warn("LIST NOT Saved");
                    throw new ListNotSavedException(newList.getListName());
                }
            }else {
                log.warn("NO Users Datails found in method createList()");
                throw new UserNotFoundException("No User Details found.");
            }
        }else{
            log.warn("Wrong data for new list");
            throw new BadNewListRequestException();
        }

    }

    public String saveBookToList(SaveBookDTO newBook, Authentication authentication){


        if(newBook.getBookId() != null && newBook.getUserListId() != null){
            UUID userId;
            CustomUserDetails details = (CustomUserDetails) authentication.getDetails();

            if (details!=null) {

                UserList list = userListRepository.findById(UUID.fromString(newBook.getUserListId())).orElseThrow(()-> new UserListNotFoundException(" "));

                userId = details.getId();
                UserEntity user = userEntityRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(" "));
                BookList book = BookList.builder().bookId(newBook.getBookId()).list(list).build();
                BookList savedBook =bookListRepository.save(book);

                if (savedBook.getIdListBook() != null) {
                    log.info("BOOK SAVED in LIST with bookId {} and listID {}", newBook.getBookId(), savedBook.getIdListBook());
                    return "Book with id"+ newBook.getBookId() + "has been saved in list " +newBook.getUserListId();
                } else {
                    throw new ListNotSavedException("");
                }
            }else {
                throw new UserNotFoundException("No User Details found.");
            }
        }else{
            throw new BadNewListRequestException();
        }
    }


}
