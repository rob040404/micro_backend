package com.users.UsersMicroservice.repositories;

import com.users.UsersMicroservice.entities.BookList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookListRepository extends JpaRepository<BookList, Long> {

}
