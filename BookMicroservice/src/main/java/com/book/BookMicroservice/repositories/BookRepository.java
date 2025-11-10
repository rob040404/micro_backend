package com.book.BookMicroservice.repositories;

import com.book.BookMicroservice.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.awt.print.Pageable;
import java.util.List;

public interface BookRepository extends JpaRepository<Book, Long> {

	//Poner métodos de búsqueda útiles según hagan falta
	
    //Búsqueda exacta title y authors
    List<Book> findByTitleIgnoreCaseAndAuthorsIgnoreCase(String title, String authors);
    //Búsqueda exacta title y authors contiene el param
    List<Book> findByTitleIgnoreCaseAndAuthorsContainsIgnoreCase(String title, String authors);
    //Búsqueda donde los param title y authors están presentes
    List<Book> findByTitleContainsIgnoreCaseAndAuthorsContainsIgnoreCase(String title, String authors);

    //Probamos paginación. NO HACE FALTA poner findAll(), LO HEREDA DE JPAREPOSITORY:
    //Page<Book> findAll(Pageable pageable);
    
    //Forma más flexible. Coincidencia exacta ignorando las mayúsculas
    @Query("SELECT b FROM Book b WHERE LOWER(b.title) = LOWER(:title)")
    List<Book> findByTitleExact(@Param("title") String title);
    
    //Coincidencia no exacta. El busca cualquier libro que contanga el parametro title en su título
    List<Book> findByTitleContainingIgnoreCase(String title);

    //@Query("SELECT b FROM Book b WHERE LOWER(b.title) LIKE LOWER(:title)") //Previamente habrá que pasar el título con los % incluidos porque np se pued hace %:title%
    //List<Book> findByTitleLike(@Param("title") String title);
    
    //Búsqueda exacta de autores
    List<Book> findByAuthorsIgnoreCase(String authors);
    
    //Búsqueda de libros por autores que estén contenidos en la columna
    List<Book> findByAuthorsContainingIgnoreCase(String authors);
    

	// Buscar por título exacto y autor específico dentro de la cadena. Esta forma de consulta nos permite se rmás flexibles
    //@Query("SELECT b FROM Book b WHERE b.title = :title AND b.authors LIKE %:author%") //La b es un alias de la entidad book
    //List<Book> findByTitleAndAuthor(@Param("title") String title, @Param("author") String author);
    
  //Consulta con JPA Rpository. Menos flexible. Por ejemplo ditingue mayúsculas de minúsculas y el usuario tendrá que hacer una búsqueda exacta
    //List<Book> findByTitle(String title);
    
}
