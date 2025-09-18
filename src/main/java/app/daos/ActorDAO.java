package app.daos;

import app.entities.Actor;
import app.entities.Movie;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class ActorDAO implements IDAO<Actor, Integer>{

    public Actor create(Actor actor, EntityManager em) {
        em.persist(actor);
        return actor;
    }

    public Actor getById(Integer id, EntityManager em) {
        return em.find(Actor.class, id);
    }

    public List<Actor> getAll(EntityManager em) {
        return em.createQuery("SELECT a FROM Actor a", Actor.class)
                .getResultList();
    }

    public Actor update(Actor actor, EntityManager em) {
        return em.merge(actor);
    }

    public boolean delete(Integer id, EntityManager em) {
        Actor actor = em.find(Actor.class, id);
        if (actor == null) return false;

        for (Movie movie : actor.getMovies()) {
            movie.getActors().remove(actor);
        }

        em.remove(actor);
        return true;
    }

    public List<Actor> getActorsByMovieId(int movieId, EntityManager em) {
        return em.createQuery("SELECT a FROM Movie m JOIN m.actors a WHERE m.id = :movieId", Actor.class)
                .setParameter("movieId", movieId)
                .getResultList();
    }
}
