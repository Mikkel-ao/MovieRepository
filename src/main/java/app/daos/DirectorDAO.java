package app.daos;

import app.entities.Director;
import app.entities.Movie;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;

import java.util.List;

public class DirectorDAO implements IDAO<Director, Integer> {

    public Director findOrCreateDirector(String name, EntityManager em) {
        try {
            Director director = em.createQuery(
                            "SELECT d FROM Director d WHERE d.name = :name",
                            Director.class)
                    .setParameter("name", name)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (director == null) {
                director = new Director();
                director.setName(name);
                em.persist(director);
            }

            return director;
        } catch (PersistenceException e) {
            throw new ApiException(500, "Database error finding or creating director: " + e.getMessage());
        }
    }

    public Director create(Director director, EntityManager em) {
        try {
            em.persist(director);
            return director;
        } catch (PersistenceException e) {
            throw new ApiException(500, "Database error creating director: " + e.getMessage());
        }
    }

    public Director getById(Integer id, EntityManager em) {
        try {
            return em.find(Director.class, id);
        } catch (PersistenceException e) {
            throw new ApiException(500, "Database error finding director by id: " + e.getMessage());
        }
    }

    public List<Director> getAll(EntityManager em) {
        try {
            return em.createQuery("SELECT d FROM Director d", Director.class)
                    .getResultList();
        } catch (PersistenceException e) {
            throw new ApiException(500, "Database error retrieving all directors: " + e.getMessage());
        }
    }

    public Director update(Director director, EntityManager em) {
        try {
            return em.merge(director);
        } catch (PersistenceException e) {
            throw new ApiException(500, "Database error updating director: " + e.getMessage());
        }
    }

    public boolean delete(Integer id, EntityManager em) {
        try {
            Director director = em.find(Director.class, id);
            if (director == null) return false;

            for (Movie movie : director.getMovies()) {
                movie.setDirector(null);
            }

            em.remove(director);
            return true;
        } catch (PersistenceException e) {
            throw new ApiException(500, "Database error deleting director: " + e.getMessage());
        }
    }

    public Director getDirectorByMovieId(int movieId, EntityManager em) {
        try {
            return em.createQuery(
                            "SELECT d FROM Movie m JOIN m.director d WHERE m.id = :movieId",
                            Director.class)
                    .setParameter("movieId", movieId)
                    .getSingleResult();
        } catch (PersistenceException e) {
            throw new ApiException(500, "Database error retrieving director by movie id: " + e.getMessage());
        }
    }
}