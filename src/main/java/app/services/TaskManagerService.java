package app.services;

import app.configs.HibernateConfig;
import app.daos.ActorDAO;
import app.daos.DirectorDAO;
import app.daos.GenreDAO;
import app.daos.MovieDAO;
import app.dtos.MovieDetailsDTO;
import app.entities.*;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManagerFactory;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
public class TaskManagerService {
    private final EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

    MovieDAO movieDAO = new MovieDAO(emf);
    DirectorDAO directorDAO = new DirectorDAO(emf);
    ActorDAO actorDAO = new ActorDAO(emf);
    GenreDAO genreDAO = new GenreDAO(emf);

    MovieService movieService = new MovieService();
    EntityMapper entityMapper = new EntityMapper();

    public void fetchAllAndStoreMovies() {
        try {
            List<MovieDetailsDTO> movieDetails = movieService.getDanishMoviesLast5Years();
            int persistedCount = 0;

            for (MovieDetailsDTO dto : movieDetails) {
                Movie movie = entityMapper.convertToMovie(List.of(dto)).get(0);

                if (dto.getCredits() != null && dto.getCredits().getCrew() != null) {
                    var directors = dto.getCredits().getCrew().stream()
                            .filter(d -> "Director".equalsIgnoreCase(d.getJob()))
                            .toList();
                    if (!directors.isEmpty()) {
                        Director director = entityMapper.convertToDirector(directors).get(0);
                        movie.setDirector(director);
                    }
                }

                if (dto.getCredits() != null && dto.getCredits().getCast() != null) {
                    var actors = entityMapper.convertToActor(dto.getCredits().getCast());
                    for (Actor actor : actors) {
                        movie.addActor(actor);
                    }
                }

                if (dto.getGenres() != null) {
                    for (var genreDTO : dto.getGenres()) {
                        Genre genre = genreDAO.findOrCreateGenre(genreDTO.getName());
                        movie.addGenre(genre);
                    }
                }

                movieDAO.create(movie);
                persistedCount++;
            }

            System.out.println("\n***** MOVIE IMPORT *****");
            System.out.println("Total number of movies persisted: " + persistedCount);

        } catch (ApiException apiException) {
            throw new RuntimeException("No data from API fetched", apiException);
        }
    }

    public void getAllMovies() {
        List<Movie> allMovies = movieDAO.getAll();
        System.out.println("\n***** ALL MOVIES *****");
        System.out.println("Total movies: " + allMovies.size());
        allMovies.forEach(m -> System.out.println("- " + m.getTitle()));
    }

    public void getDirectorAndActors(int movieId) {
        List<Actor> allActorsByMovieId = actorDAO.getActorsByMovieId(movieId);
        Director allDirectorsByMovieId = directorDAO.getDirectorByMovieId(movieId);

        System.out.println("\n***** DIRECTOR & ACTORS *****");
        System.out.println("Movie ID: " + movieId);
        System.out.println("Director: " + (allDirectorsByMovieId != null ? allDirectorsByMovieId.getName() : "None"));
        System.out.println("Actors:");
        allActorsByMovieId.forEach(a -> System.out.println("- " + a.getName()));
    }

    public void getMoviesByGenre(String genreToGet) {
        List<Movie> allMoviesByGenre = movieDAO.getMoviesByGenre(genreToGet);
        System.out.println("\n***** MOVIES BY GENRE *****");
        System.out.println("Genre: " + genreToGet);
        System.out.println("Total movies: " + allMoviesByGenre.size());
        allMoviesByGenre.forEach(m -> System.out.println("- " + m.getTitle()));
    }

    public void getAllMoviesByTitle(String titleToGet) {
        List<Movie> allMoviesByTitle = movieDAO.getMoviesByTitle(titleToGet);
        System.out.println("\n***** MOVIES BY TITLE *****");
        System.out.println("Search title: " + titleToGet);
        System.out.println("Total movies found: " + allMoviesByTitle.size());
        allMoviesByTitle.forEach(m -> System.out.println("- " + m.getTitle()));
    }

    public void getTotalAvgRatingForAllMovies() {
        double totalAvg = movieDAO.getTotalRatingForAllMovies();
        System.out.println("\n*****TOTAL AVERAGE RATING *****");
        System.out.printf("Total average rating for all movies: %.2f%n", totalAvg);
    }

    public void topTenHighestRatedMovies() {
        List<Movie> top10 = movieDAO.getHighestRatedMovies();
        System.out.println("\n*****TOP 10 HIGHEST RATED MOVIES *****");
        top10.forEach(m -> System.out.println("- " + m.getTitle() + " (Rating: " + m.getRating() + ")"));
    }

    public void topTenLowestRatedMovies() {
        List<Movie> bottom10 = movieDAO.getLowestRatedMovies();
        System.out.println("\n****** TOP 10 LOWEST RATED MOVIES *****");
        bottom10.forEach(m -> System.out.println("- " + m.getTitle() + " (Rating: " + m.getRating() + ")"));
    }

    public void topTenPopularMovies() {
        List<Movie> popular = movieDAO.getMostPopularMovies();
        System.out.println("\n***** TOP 10 MOST POPULAR MOVIES *****");
        popular.forEach(m -> System.out.println("- " + m.getTitle() + " (Score: " + m.getRating() + ")"));
    }

    /// OPTIONAL TASKS ///

    public void allMoviesWithActor(String actorToGet) {
        List<Movie> movies = movieDAO.getAllMoviesByActor(actorToGet);
        System.out.println("\n***** MOVIES WITH ACTOR *****");
        System.out.println("Actor: " + actorToGet);
        System.out.println("Total movies: " + movies.size());
        movies.forEach(m -> System.out.println("- " + m.getTitle()));
    }

    public void allMoviesWithDirector(String directorToGet) {
        List<Movie> movies = movieDAO.getAllMoviesByDirector(directorToGet);
        System.out.println("\n***** MOVIES WITH DIRECTOR *****");
        System.out.println("Director: " + directorToGet);
        System.out.println("Total movies: " + movies.size());
        movies.forEach(m -> System.out.println("- " + m.getTitle()));
    }
}
