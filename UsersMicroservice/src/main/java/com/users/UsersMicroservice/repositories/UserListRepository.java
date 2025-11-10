package com.users.UsersMicroservice.repositories;

import com.users.UsersMicroservice.entities.UserList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UserListRepository extends JpaRepository<UserList, UUID> {

}
