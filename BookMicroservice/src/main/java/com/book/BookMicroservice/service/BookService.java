package com.book.BookMicroservice.service;


import com.book.BookMicroservice.client.UserServiceClient;
import com.book.BookMicroservice.dto.BookDTOConverter;
import com.book.BookMicroservice.dto.GetBookDTO;
import com.book.BookMicroservice.dto.RequestBookIdDTO;
import com.book.BookMicroservice.entity.Book;
import com.book.BookMicroservice.entity.Rating;
import com.book.BookMicroservice.exception.BookNotFoundException;
import com.book.BookMicroservice.exception.InvalidBookSearchException;
import com.book.BookMicroservice.repositories.BookRepository;
import com.book.BookMicroservice.security.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Log4j2
@Service
@RequiredArgsConstructor
public class BookService extends BaseService<Book, Long, BookRepository>{
	
	@Autowired
	private final BookRepository  bookRepository;

    @Autowired
    private final BookDTOConverter bookDTOConverter;

    @Autowired
    private final RatingService ratingService;

    @Autowired
    private final JWTUtil jwtUtil;

    @Autowired
    private final CustomUserDetails customUserDetails;

    @Autowired
    private final UserServiceClient userServiceClient;
	
	@Override
	public Book save(Book t) {
		// TODO Auto-generated method stub
		return super.save(t);
	}

	@Override
	public Optional<Book> findById(Long id) {
		// TODO Auto-generated method stub
		return super.findById(id);
	}

	@Override
	public List<Book> findAll() {
		// TODO Auto-generated method stub
		return super.findAll();
	}

	@Override
	public Page<Book> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return super.findAll(pageable);
	}

	@Override
	public Book edit(Book t) {
		// TODO Auto-generated method stub
		return super.edit(t);
	}

	@Override
	public void delete(Book t) {
		// TODO Auto-generated method stub
		super.delete(t);
	}

	@Override
	public void deleteById(Long id) {
		// TODO Auto-generated method stub
		super.deleteById(id);
	}

	public List<Book> findBooksByTitleAndAuthors(String title, String authors){
		
	   List<Book> exactList = bookRepository.findByTitleIgnoreCaseAndAuthorsIgnoreCase(title, authors);
	   List<Book> exactTitleContainsAuthors = bookRepository.findByTitleIgnoreCaseAndAuthorsContainsIgnoreCase(title, authors);
	   List<Book> containsTitleAndAuthors = bookRepository.findByTitleContainsIgnoreCaseAndAuthorsContainsIgnoreCase(title, authors);
	   
	   //Esto funciona pra más de una lista
	   return Stream.of(exactList, exactTitleContainsAuthors, containsTitleAndAuthors)
               .flatMap(Collection::stream)
               .distinct()
               .collect(Collectors.toList());

	   /**
	    * Stream.of crea un flujo de listas.
		* flatMap transforma ese flujo de listas en un único flujo de elementos.
		* distinct elimina duplicados basándose en el método equals del objeto Book.
		* Finalmente, collect genera la lista resultante.
	    */
	   
	}

	public List<Book> findByTitle(String title){
		
		List<Book> booksList = bookRepository.findByTitleExact(title);
		List<Book> booksListLike = bookRepository.findByTitleContainingIgnoreCase(title);
		
		//A partir de JAVA 8
		return Stream.concat(booksList.stream(), booksListLike.stream())
						.distinct()
						.collect(Collectors.toList());
		
		//Para sumar las dos listas (títulos exactos y títulos aproximados) hay que hacer un set
		//Set<Book> set = new LinkedHashSet<>(booksList);
		//set.addAll(booksListLike);	//Sumamos la segunda lista al set
		
		//Volvemos a convertir en lista 
		//return new ArrayList<>(set);
	}
	
	public List<Book> findByAuthors(String authors){
		
		List<Book> exactList = bookRepository.findByAuthorsIgnoreCase(authors);
		List<Book> aproxList = bookRepository.findByAuthorsContainingIgnoreCase(authors);
		
		return Stream.concat(exactList.stream(), aproxList.stream())
				.distinct()
				.collect(Collectors.toList());
		
	}

    //Puede y debe devolver ResponseEntity, así que bien
    public ResponseEntity<?> bookSearch (GetBookDTO petitionDTO, Authentication authentication){
        if(petitionDTO.getId() != null) {
            log.debug("Id search with {}", petitionDTO.getId());

            UUID userId = null;

            //Prueba de llamada a la api User. Funciona
            //String userClientResponse = userServiceClient.getUsername(authentication);

            // ✅ Verifica que haya autenticación válida y que el principal sea tu CustomUserDetails



            if (authentication != null){
                CustomUserDetails details = (CustomUserDetails) authentication.getDetails();
                if(details != null){
                    log.debug("User Details true");
                    userId = details.getId();
                }
            }


            int rating = 0;
            String userReview = null;

            Optional<Book> book = findById(petitionDTO.getId());

            if(book.isPresent() && userId != null) {
                Book book1 = book.get();
                Optional<Rating> existingRatingOpt = ratingService.findExistingRating(userId, book1);

                if(existingRatingOpt.isPresent()) {
                    rating = existingRatingOpt.get().getRating();
                    userReview = existingRatingOpt.get().getReview();
                }
            }

            GetBookDTO getBookDTO = bookDTOConverter.toGetOptionalBookDTO(book, rating, userReview);
            return ResponseEntity.ok(getBookDTO);
        }

        else if(petitionDTO.getTitle() !=null && petitionDTO.getAuthors() != null) {
            log.debug("Title and authors search with {} and {}", petitionDTO.getTitle(), petitionDTO.getAuthors());
            List<Book> booksList = findBooksByTitleAndAuthors(petitionDTO.getTitle(), petitionDTO.getAuthors());
            List<GetBookDTO> getBookDTOList = booksList.stream().map(bookDTOConverter::toGetBookDTO).collect(Collectors.toList()); //Ver si funciona
            return ResponseEntity.ok(getBookDTOList);
        }
        //Búsqueda solo por título
        else if(petitionDTO.getTitle() !=null) {
            log.debug("Title search with {}", petitionDTO.getTitle());
            List<Book> booksList = findByTitle(petitionDTO.getTitle());
            List<GetBookDTO> getBookDTOList = booksList.stream().map(bookDTOConverter::toGetBookDTO).collect(Collectors.toList());
            return ResponseEntity.ok(getBookDTOList);
        }
        //Búsqueda solo por autores
        else if(petitionDTO.getAuthors() != null) {
            log.debug("Authors search with {}", petitionDTO.getAuthors());
            List<Book> booksList = findByAuthors(petitionDTO.getAuthors());
            List<GetBookDTO> getBookDTOList = booksList.stream().map(bookDTOConverter::toGetBookDTO).collect(Collectors.toList());
            return ResponseEntity.ok(getBookDTOList);
        }

        else {
            log.warn("Invalid book search");
            throw new InvalidBookSearchException();

            //Solo para probar la paginación
            // Define paginación directamente en el código
            /*
            int page = 0;          // primera página
            int size = 5;         // 10 libros por página
            Sort sort = Sort.by("title").ascending(); // ordenar por título

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Book> books = bookRepository.findAll(pageable);
            return ResponseEntity.ok(books);

             */
        }
    }


    public ResponseEntity<Long> sendBookId(RequestBookIdDTO request, String authHeader){

        if(request.getBookId() != null){
            Optional <Book> optBook = bookRepository.findById(request.getBookId());
            Book book = optBook.orElseThrow(() -> new BookNotFoundException(request.getBookId()));
        }else{
            throw new BookNotFoundException(request.getBookId());
        }

        return ResponseEntity.ok(request.getBookId());
    }
	
}
