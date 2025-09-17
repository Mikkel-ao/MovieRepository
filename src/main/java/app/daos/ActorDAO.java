package app.daos;

import app.entities.Actor;
import app.entities.Movie;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class ActorDAO implements IDAO<Actor, Integer> {
    private final EntityManagerFactory emf;

    public ActorDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Actor create(Actor actor) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.persist(actor);
            em.getTransaction().commit();
            return actor;
        } catch (Exception e) {
            throw new ApiException(500, "Error creating actor: " + e.getMessage());
        }
    }

    @Override
    public List<Actor> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT a FROM Actor a", Actor.class)
                    .getResultList();
        } catch (Exception e) {
            throw new ApiException(500, "Error finding actors: " + e.getMessage());
        }
    }

    @Override
    public Actor getById(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.find(Actor.class, id);
        } catch (Exception e) {
            throw new ApiException(500, "Error getting actor: " + e.getMessage());
        }
    }

    @Override
    public Actor update(Actor actor) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Actor updated = em.merge(actor);
            em.getTransaction().commit();
            return updated;
        } catch (Exception e) {
            throw new ApiException(500, "Error updating actor: " + e.getMessage());
        }
    }

    @Override
    public boolean delete(Integer id) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            Actor actor = em.find(Actor.class, id);
            if (actor == null) return false;

            // Remove the actor from all movies first to avoid issue with the ManyToMany relation
            for (Movie movie : actor.getMovies()) {
                movie.getActors().remove(actor);
            }

            em.remove(actor);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            throw new ApiException(500, "Error deleting actor: " + e.getMessage());
        }
    }

    public List<Actor> getActorsByMovieId(int movieId) {
        try (EntityManager em = emf.createEntityManager()) {
            return em.createQuery("SELECT a FROM Movie m JOIN m.actors a WHERE m.id = :movieId", Actor.class)
                    .setParameter("movieId", movieId)
                    .getResultList();
        }
    }


}
