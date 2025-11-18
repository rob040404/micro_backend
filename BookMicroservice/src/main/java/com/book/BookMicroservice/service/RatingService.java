package com.book.BookMicroservice.service;


import com.book.BookMicroservice.dto.RatingDTO;
import com.book.BookMicroservice.dto.RatingDTOConverter;
import com.book.BookMicroservice.dto.ReviewDTO;
import com.book.BookMicroservice.dto.ReviewDTOConverter;
import com.book.BookMicroservice.entity.Book;
import com.book.BookMicroservice.entity.Rating;
import com.book.BookMicroservice.exception.BookNotFoundException;
import com.book.BookMicroservice.exception.InvalidRatingException;
import com.book.BookMicroservice.exception.VoteConversionException;
import com.book.BookMicroservice.repositories.BookRepository;
import com.book.BookMicroservice.repositories.RatingRepository;
import com.book.BookMicroservice.security.JWTUtil;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
@Log4j2
@Service
@RequiredArgsConstructor
public class RatingService extends BaseService <Rating, UUID, RatingRepository> {


	private final RatingRepository ratingRepository;

    //No inyectamos BookService para obtener los métodos porque crae una dependencia circular entre BookController, RatingService y BookSrevice

    private final BookRepository bookRepository;

    private final ReviewDTOConverter reviewDTOConverter;

    private final RatingDTOConverter ratingDTOConverter;

    private final CustomUserDetails customUserDetails;


	
	@Override
	public Rating save(Rating t) {
		// TODO Auto-generated method stub
		return super.save(t);
	}

	@Override
	public Optional<Rating> findById(UUID id) {
		// TODO Auto-generated method stub
		return super.findById(id);
	}

	@Override
	public List<Rating> findAll() {
		// TODO Auto-generated method stub
		return super.findAll();
	}

	@Override
	public Page<Rating> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return super.findAll(pageable);
	}

	@Override
	public Rating edit(Rating t) {
		// TODO Auto-generated method stub
		return super.edit(t);
	}

	@Override
	public void delete(Rating t) {
		// TODO Auto-generated method stub
		super.delete(t);
	}

	@Override
	public void deleteById(UUID id) {
		// TODO Auto-generated method stub
		super.deleteById(id);
	}

    //Cambiar por user_i
	public UUID hasUserVotedBook(UUID userId, Book book) {
		 Rating rating = ratingRepository.findByUserIdAndBook(userId, book);
		 UUID id = null;
		 
		 if(rating != null) {
			 id=rating.getId();
		 }
		 
		 return id;
	}


	public Optional<Rating> findExistingRating(UUID userId, Book book) {
	    return Optional.ofNullable(ratingRepository.findByUserIdAndBook(userId, book));
	}
	
	public float calculateAverageRating(Book book) {
		
		List<Rating> ratings = ratingRepository.findRatingsNotNullByBook(book);
		
		if (ratings == null || ratings.isEmpty()) {
	        return 0.0f; 
	    }									
		
		return (float) ratings.stream()			//Convierte la lista ratings en un Stream , que te permite usar métodos funcionales como map, filter, reduce, etc. Es como abrir una tubería por donde pasan uno a uno los elementos de la lista.
				.mapToInt(Rating::getRating)	//convierte cada objeto Rating en su valor entero rating .Rating::getRating es una referencia a método  que equivale a: rating-> rating.getrating()
	            .average()						
	            .orElse(0.0f);
	}
	
	public int calculateNumVotes(Book book) {
		
		List <Rating> ratings = ratingRepository.findRatingsNotNullByBook(book);
        return ratings.size();
	}

    public ResponseEntity<?> vote(RatingDTO vote, Authentication authentication){

        if(vote.getRatingDTO()<=0 || vote.getRatingDTO()>10) {
            throw new InvalidRatingException();
        }

        CustomUserDetails details = (CustomUserDetails) authentication.getDetails();
        UUID userId = details.getId();
        String username = details.getUsername();

        Book book = bookRepository.findById(vote.getBookIdDTO())
                .orElseThrow(() -> new BookNotFoundException(vote.getBookIdDTO()));

        //We check if user vote already exists
        Optional<Rating> existingRatingOpt = findExistingRating(userId, book);

        Rating savedRating;

        if(existingRatingOpt.isPresent()) {
            Rating ratingToUpdate = existingRatingOpt.get();
            ratingToUpdate.setRating(vote.getRatingDTO());
            savedRating = save(ratingToUpdate);
        }else {
            Rating newRating = ratingDTOConverter.fromDTOtoRating(vote, userId, book, username);
            if (newRating == null) {
                throw new VoteConversionException();
            }
            savedRating = save(newRating);
        }

        float avgRating = calculateAverageRating(book);
        int numVotes = calculateNumVotes(book);

        book.setRating(avgRating);
        book.setNumVotes(numVotes);

        bookRepository.save(book);

        return ResponseEntity.status(HttpStatus.CREATED).body(ratingDTOConverter.fromRatingToDTO(savedRating));
    }


    public ReviewDTO reviewBook(ReviewDTO reviewDTO, Authentication authentication) {

        //Obtener el user_id. Lo pongo provisional
        CustomUserDetails details = (CustomUserDetails) authentication.getDetails();
        UUID userId = details.getId();
        String username = details.getUsername();

        //See if row exists in DB
        Book book = bookRepository.findById(reviewDTO.getBookIdDTO())
                .orElseThrow(()-> new BookNotFoundException(reviewDTO.getBookIdDTO()));

        //we get if this row in rating is present. We do it with mapping and orElseGet instead of conditionals
        Rating savedRating = findExistingRating(userId, book)
                .map(existingRating -> {
                    existingRating.setReview(reviewDTO.getReviewDTO());
                    log.info("UPDATED rating for bookId {} an userId {}", book.getId(), userId);
                    return save(existingRating);
                })
                .orElseGet(() -> {
                    Rating newRating = Rating.builder()
                            .book(book)
                            .userId(userId)
                            .review(reviewDTO.getReviewDTO())
                            .username(username)
                            .build();
                    log.info("SAVED rating for bookId {} an userId {}", book.getId(), userId);
                    return save(newRating);
                });

        return reviewDTOConverter.fromRatingToReviewDTO(savedRating);

    }

    public Page <ReviewDTO> getAllReviews(Long bookId, int p1, int p2){

        Book book = bookRepository.findById(bookId).orElseThrow(()-> new BookNotFoundException(bookId));

        Sort sort = Sort.by("reviewDate").descending();

        Pageable pageable = PageRequest.of(p1, p2, sort);
        Page<Rating> reviews = ratingRepository.findByBook(book, pageable);

        List <ReviewDTO> reviewDTO = reviews.getContent().stream()
                .map(row -> new ReviewDTO(row.getReview(), bookId, row.getUsername()))
                .toList();

        Page<ReviewDTO> pageReviewDto = new PageImpl<>(reviewDTO, pageable, reviews.getTotalElements());

        return pageReviewDto;

    }

}
