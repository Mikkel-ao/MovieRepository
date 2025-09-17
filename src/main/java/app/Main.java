package app;

import app.configs.HibernateConfig;
import app.daos.ActorDAO;
import app.daos.DirectorDAO;
import app.daos.GenreDAO;
import app.daos.MovieDAO;
import app.dtos.MovieDetailsDTO;
import app.entities.*;
import app.services.MovieService;
import jakarta.persistence.EntityManagerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        EntityMapper mapper = new EntityMapper();
        MovieDAO movieDAO = new MovieDAO(emf);
        GenreDAO genreDAO = new GenreDAO(emf);
        ActorDAO actorDAO = new ActorDAO(emf);
        DirectorDAO directorDAO = new DirectorDAO(emf);
        /*
        MovieService movieService = new MovieService();
        List<MovieDetailsDTO> movieDetails = movieService.getDanishMoviesLast5Years();

        for (MovieDetailsDTO dto : movieDetails) {
            Movie movie = mapper.convertToMovie(List.of(dto)).get(0);

            if (dto.getCredits() != null && dto.getCredits().getCrew() != null) {
                var directors = dto.getCredits().getCrew().stream()
                        .filter(d -> "Director".equalsIgnoreCase(d.getJob()))
                        .toList();
                if (!directors.isEmpty()) {
                    Director director = mapper.convertToDirector(directors).get(0);
                    movie.setDirector(director);
                }
            }

            if (dto.getCredits() != null && dto.getCredits().getCast() != null) {
                var actors = mapper.convertToActor(dto.getCredits().getCast());
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
*/
        //CRUD calls
/*
        //getAll method
        //List<Movie> allMovies = movieDAO.getAll();
        //allMovies.forEach(System.out::println);

        //
        List<Actor> allActorsByMovieId = actorDAO.getActorByMovieId(7);
        //allActorsByMovieId.forEach(System.out::println);

        //
        List<Director> allDirectorsByMovieId = directorDAO.getDirectorByMovieId(7);
        //allDirectorsByMovieId.forEach(System.out::println);

        //
        List<Object> allDirectorsAndActors = new ArrayList<>();
        allDirectorsAndActors.addAll(allActorsByMovieId);
        allDirectorsAndActors.addAll(allDirectorsByMovieId);
        allDirectorsAndActors.forEach(System.out::println);

        //
        String genreToGet = "Drama";
        List<Movie> allMoviesByGenre = movieDAO.getMoviesByGenre(genreToGet);
        allMoviesByGenre.forEach(System.out::println);

        //
        List<Movie> allMoviesByTitle = movieDAO.getMoviesByTitle("100");
        allMoviesByTitle.forEach(System.out::println);

        //
        double getTotalAverageForAllMovies = movieDAO.getTotalRatingForAllMovies();
        System.out.println("getTotalAverageForAllMovies: " + getTotalAverageForAllMovies);

        //
        List<Movie> top10HighestRatedMovies = movieDAO.getHighestRatedMovies();
        top10HighestRatedMovies.forEach(System.out::println);

        //
        List<Movie> top10LowestRatedMovies = movieDAO.getLowestRatedMovies();
        top10LowestRatedMovies.forEach(System.out::println);


 */
        List<Movie> mostPopularMovies = movieDAO.getMostPopularMovies();
        mostPopularMovies.forEach(System.out::println);
        emf.close();
        System.out.println("All movies persisted!");
    }
}
