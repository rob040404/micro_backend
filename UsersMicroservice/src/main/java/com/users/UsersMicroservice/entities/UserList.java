package com.users.UsersMicroservice.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;
/**
 * Represents a named list owned by a user.
 * Each list can contain multiple books (via {@link BookList}).
 */
@Entity
@Table(name="user_lists")
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class UserList {

	@Id  @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_user_list", nullable = false )
	private UUID id;
	
	//A user can create many lists
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable =false)
	private UserEntity user;
	
	@Column(name = "list_name", nullable = false)
	private String listName;

}
