package app.daos;

import app.entities.Director;
import app.entities.Movie;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class MovieDAO implements IDAO<Movie, Integer> {

    public Movie create(Movie movie, EntityManager em) {
        try {
            em.persist(movie);
            return movie;
        } catch (Exception e) {
            throw new ApiException(500, "Database error creating movie: " + e.getMessage());
        }
    }

    public Movie getById(Integer id, EntityManager em) {
        try {
            return em.find(Movie.class, id);
        } catch (Exception e) {
            throw new ApiException(500, "Database error finding movie by id: " + e.getMessage());
        }
    }

    public List<Movie> getAll(EntityManager em) {
        try {
            return em.createQuery("SELECT m FROM Movie m", Movie.class)
                    .getResultList();
        } catch (Exception e) {
            throw new ApiException(500, "Database error retrieving all movies: " + e.getMessage());
        }
    }

    public Movie update(Movie movie, EntityManager em) {
        try {
            return em.merge(movie);
        } catch (Exception e) {
            throw new ApiException(500, "Database error updating movie: " + e.getMessage());
        }
    }

    public boolean delete(Integer id, EntityManager em) {
        try {
            Movie movie = em.find(Movie.class, id);
            if (movie == null) return false;

            em.remove(movie);
            return true;
        } catch (Exception e) {
            throw new ApiException(500, "Database error deleting movie: " + e.getMessage());
        }
    }

    public double getTotalRatingForAllMovies(EntityManager em) {
        try {
            Double avgRating = em.createQuery(
                            "SELECT AVG(m.rating) FROM Movie m WHERE m.rating > 0",
                            Double.class)
                    .getSingleResult();
            return avgRating != null ? avgRating : 0.0;
        } catch (Exception e) {
            throw new ApiException(500, "Invalid query for average movie rating: " + e.getMessage());
        }
    }

    public List<Movie> getHighestRatedMovies(EntityManager em) {
        try {
            return em.createQuery(
                            "SELECT m FROM Movie m ORDER BY m.rating DESC",
                            Movie.class)
                    .setMaxResults(10)
                    .getResultList();
        } catch (Exception e) {
            throw new ApiException(500, "Invalid query for top 10 highest rated movies: " + e.getMessage());
        }
    }

    public List<Movie> getLowestRatedMovies(EntityManager em) {
        try {
            return em.createQuery(
                    "SELECT m FROM Movie m WHERE m.rating > 0.0 ORDER BY m.rating ASC", Movie.class)
                    .setMaxResults(10)
                    .getResultList();
        } catch (Exception e) {
            throw new ApiException(500, "Invalid query for top 10 lowest rated movies: " + e.getMessage());
        }
    }

    public List<Movie> getMostPopularMovies(EntityManager em) {
        try {
            return em.createQuery(
                            "SELECT m FROM Movie m ORDER BY m.popularity DESC",
                            Movie.class)
                    .setMaxResults(10)
                    .getResultList();
        } catch (Exception e) {
            throw new ApiException(500, "Invalid query for top 10 most popular movies: " + e.getMessage());
        }
    }

    public List<Movie> getAllMoviesByActor(String actorName, EntityManager em) {
        try {
            return em.createQuery(
                            "SELECT m FROM Movie m JOIN m.actors a WHERE LOWER(a.name) LIKE :actorName",
                            Movie.class)
                    .setParameter("actorName", "%" + actorName.toLowerCase() + "%")
                    .getResultList();
        } catch (Exception e) {
            throw new ApiException(500, "Invalid query or parameter for actor: " + e.getMessage());
        }
    }

    public List<Movie> getAllMoviesByDirector(String directorName, EntityManager em) {
        try {
            return em.createQuery(
                            "SELECT m FROM Movie m JOIN m.director d WHERE LOWER(d.name) LIKE :directorName",
                            Movie.class)
                    .setParameter("directorName", "%" + directorName.toLowerCase() + "%")
                    .getResultList();
        } catch (Exception e) {
            throw new ApiException(500, "Invalid query or parameter for director: " + e.getMessage());
        }
    }

    public List<Movie> getMoviesByGenre(String genreName, EntityManager em) {
        try {
            return em.createQuery(
                            "SELECT DISTINCT m FROM Movie m JOIN m.genres g WHERE g.name = :genreName",
                            Movie.class)
                    .setParameter("genreName", genreName)
                    .getResultList();
        } catch (Exception e) {
            throw new ApiException(500, "Invalid query or parameter for genre: " + e.getMessage());
        }
    }

    public List<Movie> getMoviesByTitle(String movieTitle, EntityManager em) {
        try {
            return em.createQuery(
                            "SELECT m FROM Movie m WHERE LOWER(m.title) LIKE :movieTitle",
                            Movie.class)
                    .setParameter("movieTitle", "%" + movieTitle.toLowerCase() + "%")
                    .getResultList();
        } catch (Exception e) {
            throw new ApiException(500, "Invalid query or parameter for title: " + e.getMessage());
        }
    }
}
