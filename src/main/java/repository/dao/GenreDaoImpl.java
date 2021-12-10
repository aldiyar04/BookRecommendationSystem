package repository.dao;

import repository.entity.Genre;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.Optional;

@Stateless
public class GenreDaoImpl implements GenreDao {
    @PersistenceContext(unitName = "default")
    private EntityManager entityManager;

    @Override
    public Optional<Genre> getGenreByName(String name) {
        Genre genre = null;
        try {
            genre = (Genre) entityManager.createNamedQuery("Genre.getByName")
                    .setParameter("name", name)
                    .getSingleResult();
        } catch(NoResultException ignored) {
        }
        return Optional.ofNullable(genre);
    }

    @Override
    public void saveGenre(Genre genre) {
        entityManager.persist(genre);
    }

    @Override
    public void updateGenre(Genre genre) {
        entityManager.merge(genre);
    }

    @Override
    public void deleteGenre(Genre genre) {
        entityManager.remove(genre);
    }
}
