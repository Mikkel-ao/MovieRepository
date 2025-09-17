package app.daos;

import app.entities.Actor;
import app.entities.Director;
import app.entities.Movie;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class DirectorDAO implements IDAO<Director, Integer> {
    private final EntityManagerFactory emf;

    public DirectorDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Director create(Director director) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(director);
            em.getTransaction().commit();
            return director;
        } catch (Exception e) {
            throw new ApiException(500, "Error creating director: " + e.getMessage());
        }
    }

    @Override
    public List<Director> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT d FROM Director d", Director.class)
                    .getResultList();
        } catch (Exception e) {
            throw new ApiException(500, "Error finding directors: " + e.getMessage());
        }
    }

    @Override
    public Director getById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Director.class, id);
        } catch (Exception e) {
            throw new ApiException(500, "Error getting director: " + e.getMessage());
        }
    }

    @Override
    public Director update(Director director) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Director updated = em.merge(director);
            em.getTransaction().commit();
            return updated;
        } catch (Exception e) {
            throw new ApiException(500, "Error updating director: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Director director = em.find(Director.class, id);
            if (director == null) return false;

            // Remove director reference from movies to avoid foreign key / relations issue
            for (Movie movie : director.getMovies()) {
                movie.setDirector(null); // Director should be set as null to detach from movie before deleting
            }
            em.remove(director); // Now that it is null, delete

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            throw new ApiException(500, "Error deleting director: " + e.getMessage());
        }
    }

    public Director getDirectorByMovieId(int movieId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT d FROM Movie m JOIN m.director d WHERE m.id = :movieId", Director.class)
                    .setParameter("movieId", movieId)
                    .getSingleResult();
        }
    }

}
