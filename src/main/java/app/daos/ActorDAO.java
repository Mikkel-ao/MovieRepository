package app.daos;

import app.entities.Actor;
import app.entities.Movie;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;


import java.util.List;

public class ActorDAO implements IDAO<Actor, Integer> {

    public Actor create(Actor actor, EntityManager em) {
        try {
            em.persist(actor);
            return actor;
        } catch (Exception e) {
            throw new ApiException(500, "Database error creating actor: " + e.getMessage());
        }
    }

    public Actor getById(Integer id, EntityManager em) {
        try {
            return em.find(Actor.class, id);
        } catch (Exception e) {
            throw new ApiException(500, "Database error finding actor by id: " + e.getMessage());
        }
    }

    public List<Actor> getAll(EntityManager em) {
        try {
            return em.createQuery("SELECT a FROM Actor a", Actor.class)
                    .getResultList();
        } catch (Exception e) {
            throw new ApiException(500, "Database error retrieving all actors: " + e.getMessage());
        }
    }

    public Actor update(Actor actor, EntityManager em) {
        try {
            return em.merge(actor);
        } catch (Exception e) {
            throw new ApiException(500, "Database error updating actor: " + e.getMessage());
        }
    }

    public boolean delete(Integer id, EntityManager em) {
        try {
            Actor actor = em.find(Actor.class, id);
            if (actor == null) return false;

            for (Movie movie : actor.getMovies()) {
                movie.getActors().remove(actor);
            }

            em.remove(actor);
            return true;
        } catch (Exception e) {
            throw new ApiException(500, "Database error deleting actor: " + e.getMessage());
        }
    }

    public List<Actor> getActorsByMovieId(int movieId, EntityManager em) {
        try {
            return em.createQuery(
                            "SELECT a FROM Movie m JOIN m.actors a WHERE m.id = :movieId",
                            Actor.class)
                    .setParameter("movieId", movieId)
                    .getResultList();
        } catch (Exception e) {
            throw new ApiException(500, "Database error retrieving actors by movie id: " + e.getMessage());
        }
    }
}
