package com.users.UsersMicroservice.controllers;


import com.users.UsersMicroservice.repositories.StorageService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
@RequiredArgsConstructor @Log4j2
public class FileController {
	
	/**
	 * In short, this method takes the filename from the URL, retrieves the file using the storage service,
     * determines the MIME type, and returns the file as an HTTP response. The browser or client making the request
     * will receive the file with the appropriate content type for viewing or downloading.
	 */


 	private final StorageService storageService;
	
 	/**
 	 * This method handles a request to retrieve a specific file.
     * @GetMapping(value="/files/{filename:.+}"): This URL mapping specifies that the method responds to GET requests
     * to the path /files/{filename}, where {filename} represents the name of the requested file.
     * {filename:.+} allows any file extension (.+ matches any character), ensuring that the filename takes into
     * account the period (.) and any extension (e.g., file.txt).
     * @param filename
     * @param request
     * @return
 	 */
	@GetMapping(value="/files/{filename:.+}")
	@ResponseBody
	public ResponseEntity<Resource> serveFile(@PathVariable String filename, HttpServletRequest request) { //HttpServletRequest request: Permite acceder a la solicitud HTTP completa, útil para obtener detalles como el tipo de contenido.

		Resource file = storageService.loadAsResource(filename);
		
        String contentType = null;
        
        /*
         * request.getServletContext().getMimeType(...):` Attempts to determine the MIME type of the file (for example,
         * image/png or text/plain) based on the extension.
         *
         * Exception handling: If an error occurs, the logger logs that the file type could not be determined.
         *
         * Default content type: If `contentType` remains `null`, it is set to `application/octet-stream`,
         * the generic type for binary files (generally used for downloads).
         */
        try {
            contentType = request.getServletContext().getMimeType(file.getFile().getAbsolutePath());
        } catch (IOException ex) {
            log.info("Could not determine file type.");
        }

        if(contentType == null) {
            contentType = "application/octet-stream";
        }
		
        /*
         * ResponseEntity.ok(): Creates an HTTP response with a status of 200 (OK).
         * .contentType(MediaType.parseMediaType(contentType)): Sets the content type in the HTTP response header, using the type specified above.
         * .body(file): Includes the file as the response body.
         */
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.body(file);
	}
}
