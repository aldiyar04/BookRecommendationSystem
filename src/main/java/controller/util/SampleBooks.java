package controller.util;

import repository.entity.Author;
import repository.entity.Book;
import repository.entity.Genre;

import java.util.ArrayList;
import java.util.Collections;

public class SampleBooks {
    public static Book createSampleBook() {
        Author author = new Author(null, "author", new ArrayList<>());
        Genre genre = new Genre(null, "genre", new ArrayList<>());
        Book book = Book.builder()
                .id(1L)
                .title("title")
                .description("description")
                .isbn("isbn")
                .yearPublished((short) 2000)
                .authors(Collections.singletonList(author))
                .genres(Collections.singletonList(genre))
                .build();
        author.getBooks().add(book);
        genre.getBooks().add(book);
        return book;
    }
}
