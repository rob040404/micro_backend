package com.users.UsersMicroservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Abstract class. It's OPTIONAL, but we're using it to avoid having to use repositories in controllers,
 * instead relying solely on services.
 * When using it, we need to implement the entity type, the ID type, and the repository we're using.
 * We have the basic methods of jpaRepository and can use them as needed. We'll need to create the different
 * services and extend these methods.
 */

public abstract class BaseService<T, ID, R extends JpaRepository<T, ID>> {

	@Autowired
	protected R repositorio;
	
	public T save(T t) {
		return repositorio.save(t);
	}
	
	public Optional<T> findById(ID id) {
		return repositorio.findById(id);
	}
	
	public List<T> findAll() {
		return repositorio.findAll();
	}
	
	public Page<T> findAll(Pageable pageable) {
		return repositorio.findAll(pageable);
	}
	
	public T edit(T t) {
		return repositorio.save(t);
	}
	
	public void delete(T t) {
		repositorio.delete(t);
	}
	
	public void deleteById(ID id) {
		repositorio.deleteById(id);
	}
	

	
	
}
