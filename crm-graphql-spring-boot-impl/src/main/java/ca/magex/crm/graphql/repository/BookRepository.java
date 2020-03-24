package ca.magex.crm.graphql.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ca.magex.crm.graphql.model.Book;

public interface BookRepository extends JpaRepository<Book, String> {

}