package com.users.UsersMicroservice.repositories;

import com.users.UsersMicroservice.entities.UserList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * JPA Repository for User lists
 * Provides CRUD operations and custom queries for user data.
 */
public interface UserListRepository extends JpaRepository<UserList, UUID> {

}
