package app;

import app.configs.HibernateConfig;
import app.daos.GenreDAO;
import app.daos.MovieDAO;
import app.dtos.MovieDetailsDTO;
import app.entities.*;
import app.services.MovieService;
import jakarta.persistence.EntityManagerFactory;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        EntityMapper mapper = new EntityMapper();
        MovieDAO movieDAO = new MovieDAO(emf);
        GenreDAO genreDAO = new GenreDAO(emf);

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

        emf.close();
        System.out.println("All movies persisted!");
    }
}
