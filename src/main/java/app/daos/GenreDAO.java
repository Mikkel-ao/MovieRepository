package app.daos;

import app.entities.Genre;
import app.entities.Movie;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class GenreDAO implements IDAO<Genre, Integer> {
    private final EntityManagerFactory emf;
    public GenreDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Genre create(Genre genre) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(genre);
            em.getTransaction().commit();
            return genre;
        } catch (Exception e) {
            throw new ApiException(500, "Error creating genre: " + e.getMessage());
        }
    }

    @Override
    public List<Genre> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT g FROM Genre g", Genre.class)
                    .getResultList();
        } catch (Exception e) {
            throw new ApiException(500, "Error finding genres: " + e.getMessage());
        }
    }

    @Override
    public Genre getById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Genre.class, id);
        } catch (Exception e) {
            throw new ApiException(500, "Error getting genre: " + e.getMessage());
        }
    }

    @Override
    public Genre update(Genre genre) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Genre updated = em.merge(genre);
            em.getTransaction().commit();
            return updated;
        } catch (Exception e) {
            throw new ApiException(500, "Error updating genre: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Genre genre = em.find(Genre.class, id);
            if (genre == null) return false;

            // Remove the genre from all movies first to avoid issue with the ManyToMany relation
            for (Movie movie : genre.getMovies()) {
                movie.getGenres().remove(genre);
            }

            em.remove(genre);

            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            throw new ApiException(500, "Error deleting genre: " + e.getMessage());
        }
    }
    public Genre findOrCreateGenre(String name) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Genre genre = em.createQuery(
                            "SELECT g FROM Genre g WHERE g.name = :name", Genre.class)
                    .setParameter("name", name)
                    .getResultStream()
                    .findFirst()
                    .orElse(null);

            if (genre == null) {
                genre = new Genre();
                genre.setName(name);
                em.persist(genre);
            }

            em.getTransaction().commit();
            return genre;
        }
    }
}
