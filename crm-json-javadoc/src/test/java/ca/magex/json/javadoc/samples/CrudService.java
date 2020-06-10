package ca.magex.json.javadoc.samples;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * A sample CRUD service for managing entities
 * 
 * @author magex
 */
public interface CrudService<K, T> {

	/**
	 * Find a a single entity given its id
	 * @param id the entity to find
	 * @return the entity
	 * @throws NoSuchElementException if no entity is found for the id
	 */
	public T load(K id) throws NoSuchElementException;
	
	/**
	 * Find a list of all the entities
	 * @return a list of all the entities, or an empty list if there are none.
	 */
	public List<T> findAll();
	
	/**
	 * Update an entity
	 * @param entity the entity to update
	 */
	public void update(T entity);
	
}
