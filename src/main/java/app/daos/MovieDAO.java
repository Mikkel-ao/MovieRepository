package app.daos;

import app.entities.Genre;
import app.entities.Movie;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class MovieDAO implements IDAO<Movie, Integer> {
    private final EntityManagerFactory emf;

    public MovieDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Movie create(Movie movie) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(movie);
            em.getTransaction().commit();
            return movie;
        } catch (Exception e) {
            throw new ApiException(500, "Error creating movie: " + e.getMessage());
        }
    }

    @Override
    public List<Movie> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT m FROM Movie m", Movie.class)
                    .getResultList();
        } catch (Exception e) {
            throw new ApiException(500, "Error finding movies: " + e.getMessage());
        }
    }

    @Override
    public Movie getById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Movie.class, id);
        } catch (Exception e) {
            throw new ApiException(500, "Error getting movie: " + e.getMessage());
        }
    }

    @Override
    public Movie update(Movie movie) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Movie updated = em.merge(movie);
            em.getTransaction().commit();
            return updated;
        } catch (Exception e) {
            throw new ApiException(500, "Error updating movie: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Movie movie = em.find(Movie.class, id);
            if (movie != null) {
                em.remove(movie);
            }
            em.getTransaction().commit();
            return movie != null;
        } catch (Exception e) {
            throw new ApiException(500, "Error deleting movie: " + e.getMessage());
        }
    }
    public List<Movie> getMoviesByGenre(String genreName) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT DISTINCT m FROM Movie m JOIN m.genres g WHERE g.name = :genreName",
                    Movie.class
            )
                    .setParameter("genreName", genreName)
                    .getResultList();
        }catch (Exception e) {
            throw new ApiException(500, "Error getting movies by genre: " + e.getMessage());
        }
    }
}
