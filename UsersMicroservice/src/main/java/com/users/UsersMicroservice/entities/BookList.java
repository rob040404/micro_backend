package com.users.UsersMicroservice.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name="list_books")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookList {

	@Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_list_book")
	private UUID idListBook;
	
	// Relación con UserList (una lista puede tener muchos libros)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "list_id", nullable = false)
	private UserList list;

    @Column(name = "book_id", nullable = false)
    private Long bookId;

}
