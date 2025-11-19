package com.book.BookMicroservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="ratings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {

	@Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id",nullable = false)
	private UUID id;

    /*
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false) // Nombre de la clave foránea en la tabla Rating
    private UserEntity user;
    */

    @Column(name = "user_id", nullable = false)
	private UUID userId;

    @Column(name = "username", nullable = false)
    private String username;

	@ManyToOne(fetch = FetchType.LAZY, optional = false) // Relación con Book
	@JoinColumn(name = "book_id", nullable = false) // Nombre de la clave foránea en la tabla Rating
	private Book book;
	
	@Column(name = "rating",nullable = true)
	private int rating;
	
	@Column(name = "review", nullable = true)
	private String review;

    @Column(name = "review_likes", nullable = true)
	private int reviewLikes;
	
	//Fecha de creación de la reseña para que luego aparezcan en la ficha las más recientes
    @Column(name = "review_date", nullable = true)
	private LocalDateTime reviewDate;
	
}
