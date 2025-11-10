package com.book.BookMicroservice.repositories;

import com.book.BookMicroservice.dto.response.ResponseUserDTO;
import com.book.BookMicroservice.entity.Book;
import com.book.BookMicroservice.entity.Rating;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RatingRepository extends JpaRepository<Rating, UUID> {

    //No poner '_' en búsquedas JPA porque no lo reconoce. Por lo tanto no usar propiedades con nombre tipo user_id sino userId
    Rating findByUserIdAndBook(UUID userId, Book book);
    //Optional<Rating> findByUser_IdAndBook_Id(Long userId, Long bookId);
    List <Rating> findByBook(Book book);

    Page <Rating> findByBook(Book book, Pageable pageable);

    @Query("SELECT r FROM Rating r WHERE r.book = :book AND r.rating IS NOT NULL")
    List <Rating> findRatingsNotNullByBook(@Param("book") Book book);


   // @Query("SELECT r FROM Rating r WHERE r.book = :book")
    //Page<Rating> findByBook2(@Param("book") Book book, Pageable pageable);

    
}
