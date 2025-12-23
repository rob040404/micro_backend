package com.book.BookMicroservice;

import com.book.BookMicroservice.dto.RatingDTOConverter;
import com.book.BookMicroservice.dto.response.RatingResponseDTO;
import com.book.BookMicroservice.dto.ReviewDTOConverter;
import com.book.BookMicroservice.dto.request.RatingRequestDTO;
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
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;

/**
 * Unitary tests for the services
 */
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

    private RatingService ratingService; // SIN @InjectMocks

    @BeforeEach
    void setUp() {
        // Crear manualmente la instancia con TODOS los mocks
        ratingService = new RatingService(
                ratingRepository,
                bookRepository,
                reviewDTOConverter,
                ratingDTOConverter

        );

    }

    @Test
    void vote_existingRating_updatesRatingAndReturnsDTO() {

        // ---------- Given Data ----------
        UUID userId = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UUID userId2 = UUID.randomUUID();
        UUID ratingId = UUID.fromString("323e4577-e89b-12d3-a456-426614174001");
        Long bookId = 1L;
        UUID ratingID1 = UUID.randomUUID();
        UUID ratingID2 = UUID.randomUUID();

        RatingRequestDTO voteDTO = new RatingRequestDTO();
        voteDTO.setBookIdDTO(bookId);
        voteDTO.setRatingDTO(8);

        Book book = new Book(
                1L, "Los Miserables", "Victor Hugo", "es", "desc",
                1200, 1862, "Novela", "Drama", "img.png",
                8.0, 1, LocalDateTime.now()
        );

        Rating existingRating = new Rating(ratingId, userId, "Roy", book, 8, "Great book", 0, LocalDateTime.now());
        Rating updatedRating = new Rating(ratingId, userId, "Roy", book, 9, "Great book", 0, LocalDateTime.now());

        RatingResponseDTO expectedResponse = new RatingResponseDTO();
        expectedResponse.setRatingDTO(8);
        expectedResponse.setBookIdDTO(bookId);

        // ---------- Authentication Mock----------
        Mockito.when(authentication.getDetails()).thenReturn(userDetails);
        Mockito.when(userDetails.getId()).thenReturn(userId);
        Mockito.when(userDetails.getUsername()).thenReturn("roy");

        // ---------- Repository Mock ----------
        Mockito.when(bookRepository.findById(bookId)).thenReturn(Optional.of(book));
        Mockito.when(ratingRepository.findByUserIdAndBook(userId, book)).thenReturn(Optional.of(existingRating));
        Mockito.when(ratingRepository.save(existingRating)).thenReturn(updatedRating);

        // ---------- Mock of votes in order to do the calculation of the avg anf num of votes ----------
        // Mock data
        Double expectedAvg = 9.0;
        Long expectedCount = 1L;
        Mockito.when(ratingRepository.findAverageRatingByBookId(book.getId())).thenReturn(expectedAvg);
        Mockito.when(ratingRepository.countRatingsByBookId(book.getId())).thenReturn(expectedCount);

        // ---------- Mock of DTO Conversion----------
        RatingResponseDTO expectedDTO = new RatingResponseDTO();
        expectedDTO.setRatingDTO(8);
        expectedDTO.setBookIdDTO(1L);
        Mockito.when(ratingDTOConverter.fromRatingToResponseDTO(updatedRating)).thenReturn(expectedDTO);

        // Just for development: ResponseEntity<?> response = ratingService.vote(voteDTO, authentication);

        System.out.println("=== DEBUG INFO ===");
        System.out.println("bookId value: " + bookId);
        System.out.println("bookId class: " + bookId.getClass().getName());
        System.out.println("voteDTO.getBookIdDTO(): " + voteDTO.getBookIdDTO());
        System.out.println("voteDTO.getBookIdDTO() class: " + voteDTO.getBookIdDTO());
        System.out.println("book object: " + book);
        System.out.println("bookRepository mock: " + bookRepository);
        System.out.println("ratingRepository mock: " + ratingRepository);
        System.out.println("=== END DEBUG ===");

        // Just for development:
        Optional<Book> testCall = bookRepository.findById(bookId);
        System.out.println("Direct mock call result: " + testCall);

        // ---------- Act ----------
        // When
        RatingResponseDTO finalResult = ratingService.vote(voteDTO, authentication);

        // Then
        assertThat(finalResult.getRatingDTO()).isEqualTo(expectedResponse.getRatingDTO());
        assertThat(finalResult.getBookIdDTO()).isEqualTo(expectedResponse.getBookIdDTO());


        // ---------- Verifications ----------
        Mockito.verify(ratingRepository, Mockito.times(1)).findByUserIdAndBook(userId, book);
        Mockito.verify(ratingRepository, Mockito.times(1)).save(existingRating);
        Mockito.verify(bookRepository, Mockito.times(1)).save(book);
    }

}

