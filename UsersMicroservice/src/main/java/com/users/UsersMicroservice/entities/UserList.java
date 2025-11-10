package com.users.UsersMicroservice.entities;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name="user_lists")
@NoArgsConstructor
@AllArgsConstructor //Si se usa, no cambiar de orden los parametros
@Builder @Getter @Setter
public class UserList {

	@Id  @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id_user_list", nullable = false )
	private UUID idUserList;  //Sustituimos Long en este caso para id tipo UUID
	
	//Un usuario puede tener muchas listas
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable =false)
	private UserEntity user;
	
	@Column(name = "list_name", nullable = false)
	private String listName;

    // Lombok genera este constructor solo para el Builder

    private UserList(UserEntity user, String listName) {
        this.user = user;
        this.listName = listName;
    }
}
