package com.users.UsersMicroservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Clase abstracta. Es OPCIONAL, pero la hacemos para no tener que usar los repositorios en los controladores, sino solo a través de servicios.
 * 
 * Cuando la usemos hay que imlementar el tipo de entidad, el tipo de id y el repositorio que estamos utilizando
 * 
 * Tenemos los métodos básicos de jpaRepository y podemos echar mano de ellos cuando lo necesitemos
 * 
 * Tendremos que crear los diferentes servicios y extender estos métodos
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
