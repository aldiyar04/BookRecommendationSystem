package repository;

import repository.entity.Book;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

@Stateless
public class BookRepositoryImpl implements BookRepository {
    @PersistenceContext(unitName = "default")
    private EntityManager entityManager;

    @Override
    public List<Book> getAllBooks() {
        return entityManager.createQuery("select b from Book b", Book.class)
                .setHint("org.hibernate.cacheable", true)
                .getResultList();
    }

    @Override
    public Optional<Book> getBookByIsbn(String isbn) {
        Book book = null;
        try {
            book = (Book) entityManager.createNamedQuery("Book.getByIsbn")
                    .setParameter("isbn", isbn)
                    .getSingleResult();
        } catch(NoResultException ignored) {
        }
        return Optional.ofNullable(book);
    }

    @Override
    public void saveBook(Book book) {
        entityManager.persist(book);
    }

    @Override
    public void updateBook(Book book) {
        entityManager.merge(book);
    }

    @Override
    public void deleteBook(Book book) {
        entityManager.remove(book);
    }
}
