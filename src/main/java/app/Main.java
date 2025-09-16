package app;

import app.configs.HibernateConfig;
import app.daos.ActorDAO;
import app.daos.DirectorDAO;
import app.daos.GenreDAO;
import app.daos.MovieDAO;
import app.dtos.DirectorDTO;
import app.dtos.MovieDetailsDTO;
import app.entities.Director;
import app.entities.EntityMapper;
import app.entities.Movie;
import app.services.MovieService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        try (EntityManager em = emf.createEntityManager()) {
            EntityMapper mapper = new EntityMapper();
            MovieDAO movieDAO = new MovieDAO(emf);
            MovieService movieService = new MovieService();
            DirectorDAO directorDAO = new DirectorDAO(emf);
            ActorDAO actorDAO = new ActorDAO(emf);
            GenreDAO genreDAO = new GenreDAO(emf);

            List<MovieDetailsDTO> movieDetails = movieService.getDanishMoviesLast5Years();
            List<DirectorDTO> directorDetails =
            System.out.println("Fetched " + movieDetails.size() + " movies:");

            movieDetails.forEach(movie -> {
                System.out.println(movie.getTitle() + " (" + movie.getReleaseDate() + ")");
            });
            List<Director> directors = mapper.convertToDirector(directorDetails);


            List<Movie> movies = mapper.convertToMovie(movieDetails);

            for(Movie movie : movies) {
                movieDAO.create(movie);
            }
        }
    }
}
