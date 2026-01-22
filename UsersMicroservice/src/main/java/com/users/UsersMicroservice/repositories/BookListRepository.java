package com.users.UsersMicroservice.repositories;

import com.users.UsersMicroservice.entities.BookList;
import com.users.UsersMicroservice.entities.UserList;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * JPA repository for Books in user's lists
 * Provides CRUD operations and custom queries for user data.
 */
public interface BookListRepository extends JpaRepository<BookList, Long> {

    boolean existsByListAndBookId(UserList list, Long bookId);
    long countByList(UserList list);    //counts the number of books in this list
}
