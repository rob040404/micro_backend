package com.book.BookMicroservice;

import com.book.BookMicroservice.dto.RatingDTO;
import com.book.BookMicroservice.dto.RatingDTOConverter;
import com.book.BookMicroservice.dto.ReviewDTOConverter;
import com.book.BookMicroservice.entity.Book;
import com.book.BookMicroservice.entity.Rating;
import com.book.BookMicroservice.repositories.BookRepository;
import com.book.BookMicroservice.repositories.RatingRepository;
import com.book.BookMicroservice.service.CustomUserDetails;
import com.book.BookMicroservice.service.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private RatingDTOConverter ratingDTOConverter;

    @Mock
    private ReviewDTOConverter reviewDTOConverter;

    @Mock
    private CustomUserDetails userDetails;

    @Mock
    private Authentication authentication;


    /*
    Lo hacemos sin @InjectMocks porque: @InjectMocks no estaba inyectando correctamente el bookRepository mock debido
    a la herencia de BaseService. Es una particularidad de este código por tener BaseService
     */
    private RatingService ratingService; // SIN @InjectMocks

    @BeforeEach
    void setUp() {
        // Crear manualmente la instancia con TODOS los mocks
        ratingService = new RatingService(
                ratingRepository,
                bookRepository,
                reviewDTOConverter,
                ratingDTOConverter,
                userDetails
        );

        // Si BaseService necesita el repositorio, configúralo
        ratingService.setRepositorio(ratingRepository);
    }



    @Test
    void vote_existingRating_updatesRatingAndReturnsDTO() {

        // ---------- Datos de entrada ----------
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID userId2 = UUID.randomUUID();
        UUID ratingId = UUID.fromString("323e4577-e89b-12d3-a456-426614174001");
        Long bookId = 1L;
        UUID ratingID1 = UUID.randomUUID();
        UUID ratingID2 = UUID.randomUUID();

        RatingDTO voteDTO = new RatingDTO();
        voteDTO.setBookIdDTO(bookId);
        voteDTO.setRatingDTO(8);

        Book book = new Book(
                1L, "Los Miserables", "Victor Hugo", "es", "desc",
                1200, 1862, "Novela", "Drama", "img.png",
                0f, 0, LocalDateTime.now()
        );

        Rating existingRating = new Rating(ratingId, userId, "Roy", book, 5, "Great book", 0, LocalDateTime.now());
        Rating updatedRating = new Rating(ratingId, userId, "Roy", book, 8, "Great book", 0, LocalDateTime.now());

        // ---------- Mock del Authentication ----------
        Mockito.when(authentication.getDetails()).thenReturn(userDetails);
        Mockito.when(userDetails.getId()).thenReturn(userId);
        Mockito.when(userDetails.getUsername()).thenReturn("pepe");

        // ---------- Mock del repositorio ----------
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        //Mockito.when(bookRepository.findById(any(Long.class))).thenReturn(Optional.of(book)); Para culquier Long (id)
        Mockito.when(ratingRepository.findByUserIdAndBook(userId, book)).thenReturn(existingRating);
        Mockito.when(ratingRepository.save(existingRating)).thenReturn(updatedRating);

        // ---------- Mock de Ratings para cálculo ----------
        List<Rating> ratings = List.of(
                new Rating(ratingID1, userId, "Roy", book, 8, " ", 0, LocalDateTime.now()),
                new Rating(ratingID2, userId2, "Ted", book, 9, " ", 0, LocalDateTime.now())
        );
        Mockito.when(ratingRepository.findRatingsNotNullByBook(book)).thenReturn(ratings);

        // ---------- Mock de conversión a DTO ----------
        RatingDTO expectedDTO = new RatingDTO();
        expectedDTO.setRatingDTO(8);
        Mockito.when(ratingDTOConverter.fromRatingToDTO(updatedRating)).thenReturn(expectedDTO);

        // Justo antes de: ResponseEntity<?> response = ratingService.vote(voteDTO, authentication);

        System.out.println("=== DEBUG INFO ===");
        System.out.println("bookId value: " + bookId);
        System.out.println("bookId class: " + bookId.getClass().getName());
        System.out.println("voteDTO.getBookIdDTO(): " + voteDTO.getBookIdDTO());
        System.out.println("voteDTO.getBookIdDTO() class: " + voteDTO.getBookIdDTO());
        System.out.println("book object: " + book);
        System.out.println("bookRepository mock: " + bookRepository);
        System.out.println("ratingRepository mock: " + ratingRepository);
        System.out.println("=== END DEBUG ===");

// Intenta llamar directamente al mock para verificar
        Optional<Book> testCall = bookRepository.findById(bookId);
        System.out.println("Direct mock call result: " + testCall);

        // ---------- Act ----------
        ResponseEntity<?> response = ratingService.vote(voteDTO, authentication);
        int numVotes = ratingService.calculateNumVotes(book);
        float avgRating = ratingService.calculateAverageRating(book);

        // ---------- Verificaciones ----------
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());

        RatingDTO body = (RatingDTO) response.getBody();
        assertEquals(8, body.getRatingDTO());

        // La media y número de votos calculados
        assertEquals(2, numVotes); // tamaño de la lista de ratings
        assertEquals(8.5f, avgRating); // media de 8 y 9

        Mockito.verify(ratingRepository, Mockito.times(1)).findByUserIdAndBook(userId, book);
        Mockito.verify(ratingRepository, Mockito.times(1)).save(existingRating);
        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
    }

}

