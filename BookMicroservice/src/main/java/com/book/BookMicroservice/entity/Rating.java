package com.book.BookMicroservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Rating Entity class
 */
@Entity
@Table(name="ratings")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {

	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name="id",nullable = false)
	private UUID id;

    @Column(name = "user_id", nullable = false)
	private UUID userId;

    @Column(name = "username", nullable = false)
    private String username;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "book_id", nullable = false)
	private Book book;
	
	@Column(name = "rating", nullable = true)
	private Integer rating;
	
	@Column(name = "review", nullable = true)
	private String review;

    @Column(name = "review_likes", nullable = true)
	private Integer reviewLikes;

    //Update only when a review is saved or updated
    @Column(name = "review_date", nullable = false)
	private LocalDateTime reviewDate;
	
}
