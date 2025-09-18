package app.daos;

import app.entities.Actor;
import app.entities.Director;
import app.entities.EntityMapper;
import app.entities.Movie;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class DirectorDAO implements IDAO<Director, Integer> {

    public Director findOrCreateDirector(String name, EntityManager em) {
        Director director = em.createQuery("SELECT d FROM Director d WHERE d.name = :name", Director.class)
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
    }

    public Director create(Director director, EntityManager em) {
        em.persist(director);
        return director;
    }

    public Director getById(Integer id, EntityManager em) {
        return em.find(Director.class, id);
    }

    public List<Director> getAll(EntityManager em) {
        return em.createQuery("SELECT d FROM Director d", Director.class)
                .getResultList();
    }

    public Director update(Director director, EntityManager em) {
        return em.merge(director);
    }

    public boolean delete(Integer id, EntityManager em) {
        Director director = em.find(Director.class, id);
        if (director == null) return false;

        for (Movie movie : director.getMovies()) {
            movie.setDirector(null);
        }

        em.remove(director);
        return true;
    }

    public Director getDirectorByMovieId(int movieId, EntityManager em) {
            return em.createQuery("SELECT d FROM Movie m JOIN m.director d WHERE m.id = :movieId", Director.class)
                    .setParameter("movieId", movieId)
                    .getSingleResult();
        }
    }