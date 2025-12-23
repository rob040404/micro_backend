package com.users.UsersMicroservice.service;

import com.users.UsersMicroservice.exception.StorageException;
import com.users.UsersMicroservice.exception.StorageFileNotFoundException;
import com.users.UsersMicroservice.repositories.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.stream.Stream;


/**
 * Implementation of a {@link StorageService} that stores
 * the uploaded files within the server where the application has been deployed.
 */
@Service
public class FileSystemStorageService implements StorageService {

	//File directory root path of our file store
	private final Path rootLocation;
	
	public FileSystemStorageService(@Value("${upload.root-location}") String path) {
		this.rootLocation = Paths.get(path);
	}

	/**
     * Método que inicializa el almacenamiento secundario del proyecto
     */
	@Override
	public void init() {
		 try {
	          Files.createDirectories(rootLocation);
	     }
	     catch (IOException e) {
	          throw new StorageException("Could not initialize storage", e);
	     }
		
	}

	/**
     * Method that stores a file in secondary storage from a MultipartFile object.
     * Since we associate it with the registered user, we will use the user ID as the filename.
     */
	@Override
	public String store(MultipartFile file) {
		String filename = StringUtils.cleanPath(file.getOriginalFilename());
		String extension = StringUtils.getFilenameExtension(filename);
		String justFilename = filename.replace("."+extension, "");
		//The filename in the store
		String storedFilename = System.currentTimeMillis() + "_" + justFilename + "." + extension;
		
		try {
			if(file.isEmpty()) {
				throw new StorageException("Failed to store empty file " + filename);
			}
			if(filename.contains("..")) {
				// This is a security check
				throw new StorageException("Cannot store file with relative path outside current directory"
						+ filename);
			}
			try (InputStream inputStream = file.getInputStream()){
				Files.copy(inputStream, this.rootLocation.resolve(storedFilename), StandardCopyOption.REPLACE_EXISTING);
				return storedFilename;
			}
		}
		catch(IOException e) {
			throw new StorageException("Failed to store file " + filename, e);
		}
		
		
	}

    /**
     * Method that returns the path of all files in the project's secondary storage.
     */
	@Override
	public Stream<Path> loadAll() {
		try {
			return Files.walk(this.rootLocation, 1)
					.filter(path -> !path.equals(this.rootLocation))
					.map(this.rootLocation::relativize);
		}
		catch(IOException e){
			throw new StorageException("Failed to read stored files", e);
		}
		
	}

	/**
     * A method that can load a file based on its name.
     * Returns a Path object.
     */
	@Override
	public Path load(String filename) {
		// TODO Auto-generated method stub
		return rootLocation.resolve(filename);
	}


    /**
     * A method that can load a file based on its name. Returns a Resource object.
     */
	@Override
	public Resource loadAsResource(String filename) {
		try {	
			Path file = load(filename);
			Resource resource = new UrlResource(file.toUri());
			if(resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new StorageFileNotFoundException(
						"Could not read file: " + filename);
			}
		}
		catch(MalformedURLException e) {
			throw new StorageFileNotFoundException("Could not read file: " + filename, e);
		}
	}

	@Override
	public void delete(String filename) {
		String justFilename = StringUtils.getFilename(filename);
		try {
			Path file = load(justFilename);
			Files.deleteIfExists(file);
		} catch (IOException e) {
			throw new StorageException("Error al eliminar un fichero", e);
		}
		
	}

	/**
     * Method that deletes all files from the project's secondary storage.
     */
	@Override
	public void deleteAll() {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}
	
}
