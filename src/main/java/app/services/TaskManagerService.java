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
                System.out.println("Persisted movie: " + movie.getTitle());
            }
        } catch (ApiException apiException) {
            throw new RuntimeException("No data from API fetched", apiException);
        }
    }


    public void getAllMovies() {
        List<Movie> allMovies = movieDAO.getAll();
        allMovies.forEach(System.out::println);
    }

    public void getDirectorAndActors(int movieId) {
        List<Actor> allActorsByMovieId = actorDAO.getActorsByMovieId(movieId);
        Director allDirectorsByMovieId = directorDAO.getDirectorByMovieId(movieId);
        List<Object> allDirectorsAndActors = new ArrayList<>();
        allDirectorsAndActors.addAll(allActorsByMovieId);
        allDirectorsAndActors.add(allDirectorsByMovieId);
        allDirectorsAndActors.forEach(System.out::println);
    }

    public void getMoviesByGenre(String genreToGet) {
        List<Movie> allMoviesByGenre = movieDAO.getMoviesByGenre(genreToGet);
        allMoviesByGenre.forEach(System.out::println);
    }

    public void getAllMoviesByTitle(String titleToGet) {
        List<Movie> allMoviesByTitle = movieDAO.getMoviesByTitle(titleToGet);
        allMoviesByTitle.forEach(System.out::println);
    }

    public void getTotalAvgRatingForAllMovies() {
        double getTotalAverageForAllMovies = movieDAO.getTotalRatingForAllMovies();
        System.out.println("getTotalAverageForAllMovies: " + getTotalAverageForAllMovies);
    }

    public void topTenHighestRatedMovies() {
        List<Movie> top10HighestRatedMovies = movieDAO.getHighestRatedMovies();
        top10HighestRatedMovies.forEach(System.out::println);
    }

    public void topTenLowestRatedMovies() {
        List<Movie> top10LowestRatedMovies = movieDAO.getLowestRatedMovies();
        top10LowestRatedMovies.forEach(System.out::println);
    }

    public void topTenPopularMovies() {
        List<Movie> mostPopularMovies = movieDAO.getMostPopularMovies();
        mostPopularMovies.forEach(System.out::println);
    }


    ///  OPTIONAL TASK /////

    public void allMoviesWithActor(String actorToGet) {
        List<Movie> allMoviesWithActor = movieDAO.getAllMoviesByActor(actorToGet);
        allMoviesWithActor.forEach(System.out::println);
    }

    public void allMoviesWithDirector(String directorToGet) {
        List<Movie> allMoviesWithDirector = movieDAO.getAllMoviesByDirector(directorToGet);
        allMoviesWithDirector.forEach(System.out::println);
    }

}
