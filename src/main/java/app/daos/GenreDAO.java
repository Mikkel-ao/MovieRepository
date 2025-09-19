package app.daos;

import app.entities.Genre;
import app.entities.Movie;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

import java.util.List;

public class GenreDAO implements IDAO<Genre, Integer> {

    public Genre findOrCreateGenre(String name, EntityManager em) {
        try {
            Genre genre = em.createQuery(
                            "SELECT g FROM Genre g WHERE g.name = :name",
                            Genre.class)
                    .setParameter("name", name)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (genre == null) {
                genre = new Genre();
                genre.setName(name);
                em.persist(genre);
            }

            return genre;
        } catch (PersistenceException e) {
            throw new ApiException(500, "Database error finding or creating genre: " + e.getMessage());
        }
    }

    public Genre create(Genre genre, EntityManager em) {
        try {
            em.persist(genre);
            return genre;
        } catch (PersistenceException e) {
            throw new ApiException(500, "Database error creating genre: " + e.getMessage());
        }
    }

    public Genre getById(Integer id, EntityManager em) {
        try {
            return em.find(Genre.class, id);
        } catch (PersistenceException e) {
            throw new ApiException(500, "Database error finding genre by id: " + e.getMessage());
        }
    }

    public List<Genre> getAll(EntityManager em) {
        try {
            return em.createQuery("SELECT g FROM Genre g", Genre.class)
                    .getResultList();
        } catch (PersistenceException e) {
            throw new ApiException(500, "Database error retrieving all genres: " + e.getMessage());
        }
    }

    public Genre update(Genre genre, EntityManager em) {
        try {
            return em.merge(genre);
        } catch (PersistenceException e) {
            throw new ApiException(500, "Database error updating genre: " + e.getMessage());
        }
    }

    public boolean delete(Integer id, EntityManager em) {
        try {
            Genre genre = em.find(Genre.class, id);
            if (genre == null) return false;

            for (Movie movie : genre.getMovies()) {
                movie.getGenres().remove(genre);
            }

            em.remove(genre);
            return true;
        } catch (PersistenceException e) {
            throw new ApiException(500, "Database error deleting genre: " + e.getMessage());
        }
    }
}
