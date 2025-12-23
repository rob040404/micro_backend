package com.users.UsersMicroservice.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

/**
 * Represents a book associated with a user's list.
 * In a microservice architecture, bookId references a book in another service,
 * so no foreign key constraint is enforced at the database level.
 */
@Entity
@Table(name="list_books")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class BookList {

	@Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_list_book")
	private UUID id;
	
	// Relación con UserList (una lista puede tener muchos libros)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "list_id", nullable = false)
	private UserList list;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

}
