package repository.dao;

import repository.entity.Author;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Stateless
public class AuthorDaoImpl implements AuthorDao {
    @PersistenceContext(unitName = "default")
    private EntityManager entityManager;

    @Override
    public Optional<Author> getAuthorByName(String name) {
        Author author = null;
        try {
            author = (Author) entityManager.createNamedQuery("Author.getByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch(NoResultException ignored) {
        }
        return Optional.ofNullable(author);
    }

    @Override
    public void saveAuthor(Author author) {
        entityManager.persist(author);
    }

    @Override
    public void updateAuthor(Author author) {
        entityManager.merge(author);
    }

    @Override
    public void deleteAuthor(Author author) {
        entityManager.remove(author);
    }
}
