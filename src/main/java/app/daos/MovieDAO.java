package app.daos;

import app.entities.Director;
import app.entities.Movie;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class MovieDAO implements IDAO<Movie, Integer>{

    public Movie create(Movie movie, EntityManager em) {
        em.persist(movie);
        return movie;
    }

    public Movie getById(Integer id, EntityManager em) {
        return em.find(Movie.class, id);
    }

    public List<Movie> getAll(EntityManager em) {
        return em.createQuery("SELECT m FROM Movie m", Movie.class)
                .getResultList();
    }

    public Movie update(Movie movie, EntityManager em) {
        return em.merge(movie);
    }

    public boolean delete(Integer id, EntityManager em) {
        Movie movie = em.find(Movie.class, id);
        if (movie == null) return false;

        em.remove(movie);
        return true;
    }

    public double getTotalRatingForAllMovies(EntityManager em) {
        return em.createQuery("SELECT AVG(m.rating) FROM Movie m WHERE m.rating > 0", Double.class)
                .getSingleResult();
    }

    public List<Movie> getHighestRatedMovies(EntityManager em) {
        return em.createQuery("SELECT m FROM Movie m ORDER BY m.rating DESC", Movie.class)
                .setMaxResults(10)
                .getResultList();
    }

    public List<Movie> getLowestRatedMovies(EntityManager em) {
        return em.createQuery("SELECT m FROM Movie m ORDER BY m.rating ASC", Movie.class)
                .setMaxResults(10)
                .getResultList();
    }

    public List<Movie> getMostPopularMovies(EntityManager em) {
        return em.createQuery("SELECT m FROM Movie m ORDER BY m.popularity DESC", Movie.class)
                .setMaxResults(10)
                .getResultList();
    }

    public List<Movie> getAllMoviesByActor(String actorName, EntityManager em) {
        return em.createQuery("SELECT m FROM Movie m JOIN m.actors a WHERE LOWER(a.name) LIKE :actorName", Movie.class)
                .setParameter("actorName", "%" + actorName.toLowerCase() + "%")
                .getResultList();
    }

    public List<Movie> getAllMoviesByDirector(String directorName, EntityManager em) {
        return em.createQuery("SELECT m FROM Movie m JOIN m.director d WHERE LOWER(d.name) LIKE :directorName", Movie.class)
                .setParameter("directorName", "%" + directorName.toLowerCase() + "%")
                .getResultList();
    }

    public List<Movie> getMoviesByGenre(String genreName, EntityManager em) {
            return em.createQuery("SELECT DISTINCT m FROM Movie m JOIN m.genres g WHERE g.name = :genreName",
                            Movie.class
                    )
                    .setParameter("genreName", genreName)
                    .getResultList();
    }
    public List<Movie> getMoviesByTitle(String movieTitle, EntityManager em) {
            return em.createQuery("SELECT m FROM Movie m WHERE LOWER(m.title) LIKE :movieTitle", Movie.class)
                    .setParameter("movieTitle", "%" + movieTitle.toLowerCase() + "%")
                    .getResultList();
    }
}
