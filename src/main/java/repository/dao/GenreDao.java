package repository.dao;

import repository.entity.Author;
import repository.entity.Genre;

import java.util.Optional;

public interface GenreDao {
    Optional<Genre> getGenreByName(String name);
    void saveGenre(Genre genre);
    void updateGenre(Genre genre);
    void deleteGenre(Genre genre);
}
