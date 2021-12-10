package repository.dao;

import repository.entity.Author;

import java.util.Optional;

public interface AuthorDao {
    Optional<Author> getAuthorByName(String name);
    void saveAuthor(Author author);
    void updateAuthor(Author author);
    void deleteAuthor(Author author);
}
