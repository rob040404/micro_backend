package com.book.BookMicroservice.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Clase para recuperar el email del User para hacer el rating
 */

@Getter
@Setter
@AllArgsConstructor
public class ResponseUserDTO {

    private String username;
}
