package app.services;

import app.configs.HibernateConfig;
import app.daos.ActorDAO;
import app.daos.DirectorDAO;
import app.daos.GenreDAO;
import app.daos.MovieDAO;
import app.dtos.DirectorDTO;
import app.dtos.MovieDetailsDTO;
import app.entities.*;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class TaskManagerService {

    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    MovieDAO movieDAO = new MovieDAO();
    DirectorDAO directorDAO = new DirectorDAO();
    ActorDAO actorDAO = new ActorDAO();
    GenreDAO genreDAO = new GenreDAO();

    MovieService movieService = new MovieService();
    EntityMapper entityMapper = new EntityMapper();

    // ----------------- TRANSACTIONAL METHOD -----------------
    public void fetchAllAndStoreMovies() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            List<MovieDetailsDTO> movieDetails = movieService.getDanishMoviesLast5Years();

            for (MovieDetailsDTO dto : movieDetails) {
                Movie movie = entityMapper.convertToMovie(List.of(dto)).get(0);

                // DIRECTOR
                if (dto.getCredits() != null && dto.getCredits().getCrew() != null) {
                    var directors = dto.getCredits().getCrew().stream()
                            .filter(d -> "Director".equalsIgnoreCase(d.getJob()))
                            .toList();

                    for (var directorDTO : directors) {
                        Director director = directorDAO.findOrCreateDirector(directorDTO.getName(), em);
                        movie.setDirector(director);
                    }
                }

                // ACTORS
                if (dto.getCredits() != null && dto.getCredits().getCast() != null) {
                    var actors = entityMapper.convertToActor(dto.getCredits().getCast());
                    for (Actor actor : actors) {
                        movie.addActor(actor); // actor will be persisted automatically if new
                    }
                }

                // GENRES
                if (dto.getGenres() != null) {
                    for (var genreDTO : dto.getGenres()) {
                        Genre genre = genreDAO.findOrCreateGenre(genreDTO.getName(), em);
                        movie.addGenre(genre);
                    }
                }

                // PERSIST MOVIE
                movieDAO.create(movie, em);
                System.out.println("Persisted movie: " + movie.getTitle());
            }

            em.getTransaction().commit();
        } catch (ApiException e) {
            em.getTransaction().rollback();
            throw new RuntimeException("No data from API fetched", e);
        } catch (Exception e) {
            em.getTransaction().rollback();
            throw new RuntimeException("Error storing movies", e);
        } finally {
            em.close();
        }
    }


    // ----------------- READ METHODS -----------------
    public void getAllMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            List<Movie> allMovies = movieDAO.getAll(em);
            allMovies.forEach(System.out::println);
        }
    }

    public void getDirectorAndActors(int movieId) {
        try (EntityManager em = emf.createEntityManager()) {
            List<Actor> actors = actorDAO.getActorsByMovieId(movieId, em);
            Director director = directorDAO.getDirectorByMovieId(movieId, em); // you need to refactor DAO to accept em
            List<Object> all = new ArrayList<>();
            all.addAll(actors);
            all.add(director);
            all.forEach(System.out::println);
        }
    }

    public void getMoviesByGenre(String genreToGet) {
        try (EntityManager em = emf.createEntityManager()) {
            List<Movie> movies = movieDAO.getMoviesByGenre(genreToGet, em);
            movies.forEach(System.out::println);
        }
    }

    public void getAllMoviesByTitle(String titleToGet) {
        try (EntityManager em = emf.createEntityManager()) {
            List<Movie> movies = movieDAO.getMoviesByTitle(titleToGet, em);
            movies.forEach(System.out::println);
        }
    }

    public void getTotalAvgRatingForAllMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            double avg = movieDAO.getTotalRatingForAllMovies(em);
            System.out.println("Average rating: " + avg);
        }
    }

    public void topTenHighestRatedMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            List<Movie> movies = movieDAO.getHighestRatedMovies(em);
            movies.forEach(System.out::println);
        }
    }

    public void topTenLowestRatedMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            List<Movie> movies = movieDAO.getLowestRatedMovies(em);
            movies.forEach(System.out::println);
        }
    }

    public void topTenPopularMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            List<Movie> movies = movieDAO.getMostPopularMovies(em);
            movies.forEach(System.out::println);
        }
    }

    public void allMoviesWithActor(String actorToGet) {
        try (EntityManager em = emf.createEntityManager()) {
            List<Movie> movies = movieDAO.getAllMoviesByActor(actorToGet, em);
            movies.forEach(System.out::println);
        }
    }

    public void allMoviesWithDirector(String directorToGet) {
        try (EntityManager em = emf.createEntityManager()) {
            List<Movie> movies = movieDAO.getAllMoviesByDirector(directorToGet, em);
            movies.forEach(System.out::println);
        }
    }

}
