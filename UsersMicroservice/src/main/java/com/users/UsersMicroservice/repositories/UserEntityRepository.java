package com.users.UsersMicroservice.repositories;

import com.users.UsersMicroservice.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA Repository for UserEntity
 * Provides CRUD operations and custom queries for user data.
 */
public interface UserEntityRepository extends JpaRepository<UserEntity, UUID> {
	
	Optional<UserEntity> findByUsername(String username);
	
	Optional <UserEntity> findByEmail(String email);

}
