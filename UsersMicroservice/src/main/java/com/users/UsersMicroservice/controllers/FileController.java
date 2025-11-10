package com.users.UsersMicroservice.controllers;


import com.users.UsersMicroservice.repositories.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;


@Controller
@RequiredArgsConstructor
public class FileController {
	
	/**
	 * Ver toda la descripción de esta clase en mis apuntes de API
	 * 
	 * En conjunto, este método toma el nombre del archivo de la URL, obtiene el archivo usando el servicio de almacenamiento, 
	 * determina el tipo MIME y devuelve el archivo como respuesta HTTP. El navegador o cliente que realiza la solicitud recibirá 
	 * el archivo con el tipo de contenido adecuado para visualización o descarga.
	 */

	/**
	 * Un objeto Logger para registrar información en los registros del sistema (log). 
	 * Aquí se usa para registrar errores o información sobre el proceso de determinación del tipo de contenido.
	 */
	private static final Logger logger = LoggerFactory.getLogger(FileController.class);
	
 	private final StorageService storageService;
	
 	/**
 	 * Este metodo se encarga de procesar una solicitud para obtener un archivo específico.
 	 * @GetMapping(value="/files/{filename:.+}"): Este mapeo de URL especifica que el método responde a solicitudes GET 
 	 * a la ruta /files/{filename}, donde {filename} representa el nombre del archivo solicitado. 
 	 * {filename:.+} permite cualquier extensión de archivo (.+ coincide con cualquier carácter), asegurando que el nombre de
 	 * archivo tenga en cuenta el punto (.) y cualquier extensión (por ejemplo, file.txt).
 	 * @param filename
 	 * @param request
 	 * @return
 	 */
	@GetMapping(value="/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename, HttpServletRequest request) { //HttpServletRequest request: Permite acceder a la solicitud HTTP completa, útil para obtener detalles como el tipo de contenido.

		
		Resource file = storageService.loadAsResource(filename);
		
        String contentType = null;
        
        /**
         * request.getServletContext().getMimeType(...): Intenta determinar el tipo MIME del archivo 
         * (por ejemplo, image/png o text/plain) según la extensión. 
         * 
         * Manejo de excepción: Si hay un error, el logger registra que no se pudo determinar el tipo de archivo.
         * 
         * Tipo de contenido predeterminado: Si contentType sigue siendo null, se establece en application/octet-stream, 
         * el tipo genérico para archivos binarios (usado generalmente para descargas).
         */
        try {
            contentType = request.getServletContext().getMimeType(file.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        if(contentType == null) {
            contentType = "application/octet-stream";
        }
		
        /**
         * ResponseEntity.ok(): Crea una respuesta HTTP con estado 200 (OK).
         * 
         * .contentType(MediaType.parseMediaType(contentType)): Establece el tipo de contenido en el encabezado de la respuesta HTTP, 
         * utilizando el tipo determinado anteriormente.
         * 
         * .body(file): Incluye el archivo como el cuerpo de la respuesta.
         */
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.body(file);
	}
}
