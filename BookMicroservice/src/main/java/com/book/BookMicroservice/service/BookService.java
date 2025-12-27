package com.book.BookMicroservice.service;


import com.book.BookMicroservice.client.UserServiceClient;
import com.book.BookMicroservice.dto.BookDTOConverter;
import com.book.BookMicroservice.dto.response.BookResponseDTO;
import com.book.BookMicroservice.dto.request.BookRequestDTO;
import com.book.BookMicroservice.entity.Book;
import com.book.BookMicroservice.entity.Rating;
import com.book.BookMicroservice.exception.BookNotFoundException;
import com.book.BookMicroservice.exception.InvalidBookSearchException;
import com.book.BookMicroservice.repositories.BookRepository;
import com.book.BookMicroservice.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Service
@RequiredArgsConstructor
public class BookService{
	

	private final BookRepository  bookRepository;
    private final BookDTOConverter bookDTOConverter;
    private final RatingService ratingService;
    private final JWTUtil jwtUtil;
    private final UserServiceClient userServiceClient;

    //The class BaseService is extended here, so its methods can be used




    /**
     * Method that is called from the searchBook controller
     * @param petitionDTO Petition that comes from the frontend with the book information
     * @param authentication We see if user is loged in so we can send his/her vote and review if ir exists
     * @return Returns a ResponseEntity with BookResponseDTO or a List of BookResponseDTOs, depends on the situation
     */
    public ResponseEntity<?> bookSearch (BookRequestDTO petitionDTO, Authentication authentication){
        // Book by id. We go to the method that extracts the book by id, and here we return the ResponseEntity with
        // BookResponseBodyDTO
        if(petitionDTO.getId() != null) {
           return ResponseEntity.ok(searchBookById(petitionDTO, authentication));
        }
        //By title and authors. We go to the method that finds the book by title and author and here we return the
        // ResponseEntity with List<BookResponseBodyDTO>
        else if(petitionDTO.getTitle() !=null && petitionDTO.getAuthors() != null
                && !petitionDTO.getTitle().trim().isEmpty() && !petitionDTO.getAuthors().trim().isEmpty()) {
            return ResponseEntity.ok(searchBookByTitleAndAuthors(petitionDTO));
        }
        // Only by title. We go to the method that finds the book ONLY by title and here we return the
        // ResponseEntity with List<BookResponseBodyDTO>
        else if( petitionDTO.getTitle() !=null && !petitionDTO.getTitle().trim().isEmpty()) {
            return ResponseEntity.ok(searchBookByTitle(petitionDTO));
        }
        // Only by authors.  We go to the method that finds the book ONLY by authors and here we return the
        // ResponseEntity with List<BookResponseBodyDTO>
        else if(petitionDTO.getAuthors() != null && !petitionDTO.getAuthors().trim().isEmpty()) {
            return ResponseEntity.ok(searchBookByAuthors(petitionDTO));
        }
        //In case none of the above situations are valid
        else {
            log.warn("Invalid book search");
            throw new InvalidBookSearchException();
        }
    }

    /**
     * Method that finds the book by id and if the user is logged also adds his/her vote and review if they exist
     * @param petitionDTO
     * @param authentication
     * @return BookResponseDTO
     */
    public BookResponseDTO searchBookById(BookRequestDTO petitionDTO, Authentication authentication){

        if(petitionDTO.getId()== null){
            throw new InvalidBookSearchException();
        }
        log.debug("Id search with {}", petitionDTO.getId());

        UUID userId = null;
        if(authentication !=null){
            userId = getUserId(authentication);
        }

        int rating = 0;
        String userReview = null;

        Book book = bookRepository.findById(petitionDTO.getId())
                .orElseThrow(()->new BookNotFoundException());

        if(userId != null){
            Optional <Rating> existingRatingOpt = ratingService.findExistingRating(userId, book);
            if(existingRatingOpt.isPresent()) {
                //If users rating/vote is null we send 0, because that's how the frontend manage it
                rating = Optional.ofNullable(existingRatingOpt.get().getRating()).orElse(0);
                userReview = existingRatingOpt.get().getReview();
            }
        }

        return bookDTOConverter.toBookResponseDTO(book, rating, userReview);
    }

    //¡¡IMPORTANT!! Some of these methods will be converted to return Pagable in the near future

    /**
     * Method that finds the book when both title and authors are specified in the BookRequestDTO
     * @param petitionDTO
     * @return A List of BookResponseDTOs
     */
    public List<BookResponseDTO> searchBookByTitleAndAuthors(BookRequestDTO petitionDTO){
        if(petitionDTO.getAuthors().isBlank() || petitionDTO.getTitle().isBlank()){
            throw new InvalidBookSearchException();
        }
        log.debug("Title and authors search with {} and {}", petitionDTO.getTitle(), petitionDTO.getAuthors());
        List<Book> booksList = findBooksByTitleAndAuthors(petitionDTO.getTitle(), petitionDTO.getAuthors());
        return booksList.stream().map(bookDTOConverter::toBookResponseListDTO).collect(Collectors.toList());
    }

    /**
     * Method that finds the book when only the title is specified in the BookRequestDTO
     * @param petitionDTO
     * @return A List of BookResponseDTOs
     */
    public List<BookResponseDTO> searchBookByTitle(BookRequestDTO petitionDTO){
        log.debug("Title search with {}", petitionDTO.getTitle());
        if(petitionDTO.getTitle().isBlank()){
            throw new InvalidBookSearchException();
        }
        List<Book> booksList = findByTitle(petitionDTO.getTitle());
        return booksList.stream().map(bookDTOConverter::toBookResponseListDTO).collect(Collectors.toList());
    }

    /**
     * Method that finds the book when only the authors field is specified in the BookRequestDTO
     * @param petitionDTO
     * @return A List of BookResponseDTOs
     */
    public List<BookResponseDTO> searchBookByAuthors(BookRequestDTO petitionDTO){
        if(petitionDTO.getAuthors().isBlank()){
            throw new InvalidBookSearchException();
        }
        log.debug("Authors search with {}", petitionDTO.getAuthors());
        List<Book> booksList = findByAuthors(petitionDTO.getAuthors());
        return booksList.stream().map(bookDTOConverter::toBookResponseListDTO).collect(Collectors.toList());
    }

    //Util Methods for the previous ones

    public List<Book> findBooksByTitleAndAuthors(String title, String authors){
        if(title.isBlank() || authors.isBlank()){
            throw new InvalidBookSearchException();
        }

        List<Book> exactList = bookRepository.findByTitleIgnoreCaseAndAuthorsIgnoreCase(title, authors);
        List<Book> exactTitleContainsAuthors = bookRepository.findByTitleIgnoreCaseAndAuthorsContainsIgnoreCase(title, authors);
        List<Book> containsTitleAndAuthors = bookRepository.findByTitleContainsIgnoreCaseAndAuthorsContainsIgnoreCase(title, authors);

        return Stream.of(exactList, exactTitleContainsAuthors, containsTitleAndAuthors)
                .flatMap(Collection::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Book> findByTitle(String title){

        List<Book> booksList = bookRepository.findByTitleExact(title);
        List<Book> booksListLike = bookRepository.findByTitleContainingIgnoreCase(title);

        return Stream.concat(booksList.stream(), booksListLike.stream())
                .distinct()
                .collect(Collectors.toList());

    }

    public List<Book> findByAuthors(String authors){

        List<Book> exactList = bookRepository.findByAuthorsIgnoreCase(authors);
        List<Book> aproxList = bookRepository.findByAuthorsContainingIgnoreCase(authors);

        return Stream.concat(exactList.stream(), aproxList.stream())
                .distinct()
                .collect(Collectors.toList());

    }

    public UUID getUserId(Authentication authentication) {

        if (authentication == null) return null;

        if (authentication.getDetails() instanceof CustomUserDetails user) {
            return user.getId();
        }

        return null;
    }
}

