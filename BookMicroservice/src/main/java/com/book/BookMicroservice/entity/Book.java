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

@Entity
@Table(name="books")
@Data
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @Column(name="id",nullable= false)
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="title",nullable= false) //hacer con todas las columnas y que coincida con el nombre de la columna. Seguridad de JPA
	private String title;
    @Column(name="authors",nullable= false)
	private String authors;
    @Column(name="lang",nullable= true)
	private String lang;

    @Lob	//Hace que la columna en la BD pueda almacenar textos largos, como TEXT o LONGTEXT
    @Column(name="description",nullable= false, columnDefinition = "LONGTEXT") //Poner siempre columnsDefinition cuando se usa Lob
	private String description;
    @Column(name="pages",nullable= true)
	private int pages; //Cuidado, cada editorial es diferente
    @Column(name="year",nullable= true)
	private int year; //Año de publicación si se puede. Lo mismo hay que cambiarlo a String si hay casos donde es indefinido o un intervalo
	
	//Poner @Lob si no coge todos los géneros. Genre es solo uno (novela, teatro, etc)
    @Column(name="genres",nullable= true)
	private String genres; //poner bien genres
    @Column(name="subjects",nullable= true)
	private String subjects;
    @Column(name="image",nullable= true)
	private String image;
	
	
	@Column(name="rating",nullable = true)
	private Float rating; //si es float no admite null, y en este caso necesitamos que si no hay valoraciones esté en null
    @Column(name="numVotes",nullable = true)
	private Integer numVotes;


	@CreatedDate @Column(name="createdAt",nullable = true)
	private LocalDateTime createdAt;

	
}
