package app.services;

import app.configs.HibernateConfig;
import app.daos.ActorDAO;
import app.daos.DirectorDAO;
import app.daos.GenreDAO;
import app.daos.MovieDAO;
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


    public void fetchAllAndStoreMovies() {
        EntityManager em = emf.createEntityManager();
        int persistedCount = 0;

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
                        movie.addActor(actor);
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
                persistedCount++;
            }

            em.getTransaction().commit();

            // Print total number of persisted movies only
            System.out.println("Total number of movies persisted: " + persistedCount);

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


    public void getAllMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            List<Movie> allMovies = movieDAO.getAll(em);
            System.out.println("\n***** ALL MOVIES *****");
            System.out.println("Total movies: " + allMovies.size());
            allMovies.forEach(m -> System.out.println("- " + m.getTitle()));

        }
    }

    public void getDirectorAndActors(int movieId) {
        try (EntityManager em = emf.createEntityManager()) {
            List<Actor> allActorsByMovieId = actorDAO.getActorsByMovieId(movieId, em);
            Director allDirectorsByMovieId = directorDAO.getDirectorByMovieId(movieId, em);

            System.out.println("\n***** DIRECTOR & ACTORS *****");
            System.out.println("Movie ID: " + movieId);
            System.out.println("Director: " + (allDirectorsByMovieId != null ? allDirectorsByMovieId.getName() : "None"));
            System.out.println("Actors:");
            allActorsByMovieId.forEach(a -> System.out.println("- " + a.getName()));
        }
    }

    public void getMoviesByGenre(String genreToGet) {
        try (EntityManager em = emf.createEntityManager()) {
            List<Movie> allMoviesByGenre = movieDAO.getMoviesByGenre(genreToGet, em);
            System.out.println("\n***** MOVIES BY GENRE *****");
            System.out.println("Genre: " + genreToGet);
            System.out.println("Total movies: " + allMoviesByGenre.size());
            allMoviesByGenre.forEach(m -> System.out.println("- " + m.getTitle()));
        }
    }

    public void getAllMoviesByTitle(String titleToGet) {
        try (EntityManager em = emf.createEntityManager()) {
            List<Movie> allMoviesByTitle = movieDAO.getMoviesByTitle(titleToGet, em);
            System.out.println("\n***** MOVIES BY TITLE *****");
            System.out.println("Search title: " + titleToGet);
            System.out.println("Total movies found: " + allMoviesByTitle.size());
            allMoviesByTitle.forEach(m -> System.out.println("- " + m.getTitle()));
        }
    }

    public void getTotalAvgRatingForAllMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            double totalAvg = movieDAO.getTotalRatingForAllMovies(em);
            System.out.println("\n***** TOTAL AVERAGE RATING *****");
            System.out.printf("Total average rating for all movies: %.2f%n", totalAvg);
        }
    }

    public void topTenHighestRatedMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            List<Movie> top10 = movieDAO.getHighestRatedMovies(em);
            System.out.println("\n***** TOP 10 HIGHEST RATED MOVIES *****");
            top10.forEach(m -> System.out.println("- " + m.getTitle() + " (Rating: " + m.getRating() + ")"));
        }
    }

    public void topTenLowestRatedMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            List<Movie> bottom10 = movieDAO.getLowestRatedMovies(em);
            System.out.println("\n****** TOP 10 LOWEST RATED MOVIES *****");
            bottom10.forEach(m -> System.out.println("- " + m.getTitle() + " (Rating: " + m.getRating() + ")"));
        }
    }

    public void topTenPopularMovies () {
        try (EntityManager em = emf.createEntityManager()) {
            List<Movie> popular = movieDAO.getMostPopularMovies(em);
            System.out.println("\n***** TOP 10 MOST POPULAR MOVIES *****");
            popular.forEach(m -> System.out.println("- " + m.getTitle() + " (Score: " + m.getPopularity() + ")"));
        }
    }

    /// OPTIONAL TASKS ///

    public void allMoviesWithActor(String actorToGet) {
        try (EntityManager em = emf.createEntityManager()) {
            List<Movie> movies = movieDAO.getAllMoviesByActor(actorToGet, em);
            System.out.println("\n***** MOVIES WITH ACTOR *****");
            System.out.println("Actor: " + actorToGet);
            System.out.println("Total movies: " + movies.size());
            movies.forEach(m -> System.out.println("- " + m.getTitle()));
        }
    }

    public void allMoviesWithDirector(String directorToGet) {
        try (EntityManager em = emf.createEntityManager()) {
            List<Movie> movies = movieDAO.getAllMoviesByDirector(directorToGet, em);
            System.out.println("\n***** MOVIES WITH DIRECTOR *****");
            System.out.println("Director: " + directorToGet);
            System.out.println("Total movies: " + movies.size());
            movies.forEach(m -> System.out.println("- " + m.getTitle()));
        }
    }

}
