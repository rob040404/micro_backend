package com.book.BookMicroservice.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entity Book
 */
@Entity
@Table(name="books")
@Data
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class Book {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id",nullable= false)
	private Long id;
	
	@Column(name="title",nullable= false)
	private String title;
    @Column(name="authors",nullable= false)
	private String authors;
    @Column(name="lang",nullable= true)
	private String lang;

    @Lob	//It allows the column in the database to store long texts, such as TEXT or LONGTEXT
    @Column(name="description", columnDefinition = "TEXT")
	private String description;
    @Column(name="pages", nullable= true)
	private Integer pages;
    @Column(name="year",nullable= true)
	private Integer year;

	//Use @Lob if it doesn't recognize all genres. Genre is only one (novel, theater, etc.)
    @Column(name="genres",nullable= true)
	private String genres;
    @Column(name="subjects",nullable= true)
	private String subjects;
    @Column(name="image",nullable= true)
	private String image;
	
	
	@Column(name="rating",nullable = true)
	private Double rating;
    @Column(name="numVotes",nullable = true)
	private Integer numVotes;


	@CreatedDate @Column(name="createdAt",nullable = false, updatable = false)
	private LocalDateTime createdAt;

	
}
