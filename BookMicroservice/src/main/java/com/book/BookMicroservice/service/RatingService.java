package com.book.BookMicroservice.service;


import com.book.BookMicroservice.dto.RatingDTOConverter;
import com.book.BookMicroservice.dto.request.ReviewRequestDTO;
import com.book.BookMicroservice.dto.response.RatingResponseDTO;
import com.book.BookMicroservice.dto.response.ReviewResponseDTO;
import com.book.BookMicroservice.dto.ReviewDTOConverter;
import com.book.BookMicroservice.dto.request.RatingRequestDTO;
import com.book.BookMicroservice.entity.Book;
import com.book.BookMicroservice.entity.Rating;
import com.book.BookMicroservice.exception.BookNotFoundException;
import com.book.BookMicroservice.exception.InvalidRatingException;
import com.book.BookMicroservice.repositories.BookRepository;
import com.book.BookMicroservice.repositories.RatingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
@Log4j2
@Service
@RequiredArgsConstructor
public class RatingService {


	private final RatingRepository ratingRepository;

    //No inyectamos BookService para obtener los métodos porque crea una dependencia circular entre BookController, RatingService y BookSrevice

    private final BookRepository bookRepository;

    private final ReviewDTOConverter reviewDTOConverter;

    private final RatingDTOConverter ratingDTOConverter;


    /**
     * Method that saves or updates users vote.
     * @param vote Information including user's id, book's id and vote
     * @param authentication User's details
     * @return RatingResponseDTO
     */
    @Transactional
    public RatingResponseDTO vote(RatingRequestDTO vote, Authentication authentication){

        if(vote.getRatingDTO()<=0 || vote.getRatingDTO()>10) {
            throw new InvalidRatingException();
        }

        //We extract user's id and username from the details
        CustomUserDetails details = (CustomUserDetails) authentication.getDetails();
        UUID userId = details.getId();
        String username = details.getUsername();

        //We extract the book from the database
        Book book = bookRepository.findById(vote.getBookIdDTO())
                .orElseThrow(() -> new BookNotFoundException());

        //We check if user vote already exists
        Optional<Rating> existingRatingOpt = findExistingRating(userId, book);

        Rating savedRating;

        //If rating exists, we update it, if it doesn't we save it
        if(existingRatingOpt.isPresent()) {
            Rating ratingToUpdate = existingRatingOpt.get();
            ratingToUpdate.setRating(vote.getRatingDTO());
            savedRating = ratingRepository.save(ratingToUpdate);
        }else {
            Rating newRating = ratingDTOConverter.fromDTOtoRating(vote, userId, book, username);
            savedRating = ratingRepository.save(newRating);
        }

        //Now we update the average rating of this book and save it.

        Double avgRating = ratingRepository.findAverageRatingByBookId(book.getId());
        Long numVotesLong = ratingRepository.countRatingsByBookId(book.getId());
        Integer numVotes = numVotesLong != null ? numVotesLong.intValue() : null;

        book.setRating(avgRating);
        book.setNumVotes(numVotes);
        bookRepository.save(book);

        return ratingDTOConverter.fromRatingToResponseDTO(savedRating);
    }

    /**
     * Method caller by the controller to save a textual review from the user for a specific book
     * @param reviewRequestDTO DTO with the information of the user, book and rating
     * @param authentication User's details
     * @return ReviewResponseDTO
     */
    public ReviewResponseDTO reviewBook(ReviewRequestDTO reviewRequestDTO, Authentication authentication) {

        CustomUserDetails details = (CustomUserDetails) authentication.getDetails();
        UUID userId = details.getId();
        String username = details.getUsername();

        //See if row exists in DB
        Book book = bookRepository.findById(reviewRequestDTO.getBookIdDTO())
                .orElseThrow(()-> new BookNotFoundException());

        //We get if this row in rating is present. We do it with mapping (from Optional) and orElseGet instead of conditionals
        //If it exists we update only the review with the setter, if not we crate a new rating with the review and the
        // rest of the information
        Rating savedRating = findExistingRating(userId, book)
                .map(existingRating -> {
                    existingRating.setReview(reviewRequestDTO.getReviewDTO());
                    existingRating.setReviewDate(LocalDateTime.now());
                    log.info("UPDATED rating");
                    return ratingRepository.save(existingRating);
                })
                .orElseGet(() -> {
                    Rating newRating = Rating.builder()
                            .book(book)
                            .userId(userId)
                            .review(reviewRequestDTO.getReviewDTO())
                            .username(username)
                            .reviewDate(LocalDateTime.now())
                            .build();
                    log.info("SAVED rating with review");
                    return ratingRepository.save(newRating);
                });

        return reviewDTOConverter.fromRatingToReviewDTO(savedRating);

    }

    /**
     * Retrieves all reviews for a book with pagination.
     *
     * @param bookId the ID of the book
     * @param page   the page number (0-based)
     * @param size   the number of reviews per page (max 100)
     * @return a page of {@link ReviewResponseDTO}
     */
    public Page <ReviewResponseDTO> getAllReviews(Long bookId, int page, int size){

        // Validate and cap page size
        if (size <= 0) size = 10;
        if (size > 100) size = 100;

        // Check book exists
        if (!bookRepository.existsById(bookId)) {
            throw new BookNotFoundException();
        }

        Sort sort = Sort.by("reviewDate").descending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Rating> ratings = ratingRepository.findByBookId(bookId, pageable);

        List<ReviewResponseDTO> content = ratings.getContent().stream()
                .map(rating -> new ReviewResponseDTO(
                        rating.getReview(),
                        bookId,
                        rating.getUsername()
                ))
                .toList();

        return new PageImpl<>(content, pageable, ratings.getTotalElements());

    }


    public Optional<Rating> findExistingRating(UUID userId, Book book) {
        return ratingRepository.findByUserIdAndBook(userId, book);
    }

}
