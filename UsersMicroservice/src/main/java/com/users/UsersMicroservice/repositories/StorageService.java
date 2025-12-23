package com.users.UsersMicroservice.repositories;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;


/**
 * This interface allows us to define an abstraction of what a secondary information store should be,
 * so that we can use it in a controller.
 * 
 * In this way, we can use a storage device that accesses our file system, or we could also implement one
 * located on a remote system, store the files in a GridFS system, ...
 *
 */

public interface StorageService {

	void init();

    String store(MultipartFile file);

    Stream<Path> loadAll();

    Path load(String filename);

    Resource loadAsResource(String filename);
    
    void delete(String filename);

    void deleteAll();
}
