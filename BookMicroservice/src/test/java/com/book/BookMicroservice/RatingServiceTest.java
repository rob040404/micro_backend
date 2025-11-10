package com.book.BookMicroservice;

import com.book.BookMicroservice.entity.Book;
import com.book.BookMicroservice.entity.Rating;
import com.book.BookMicroservice.repositories.RatingRepository;
import com.book.BookMicroservice.service.RatingService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class RatingServiceTest {
    /*
    @Mock
    private RatingRepository ratingRepository;
    //Hemos mockeado el repositorio

    @InjectMocks
    private RatingService ratingService;

    public RatingServiceTest(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void voteRating(){
        Book book = new Book(1, "Los Miserables", "Victor Hugo", "es", "Mu bueno", 902, 1862, "Novela", "Drama", " ", 9.2, 403);
        Rating rating = ratingRepository.findByUserIdAndBook(1, book);

        assertNotNull(rating); //Compara que no sea null e valor
        assertEquals(203, rating.getUserId());
    }


     */

}
