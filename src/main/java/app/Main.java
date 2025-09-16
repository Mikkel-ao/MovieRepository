package app;

import app.dtos.MovieDetailsDTO;
import app.services.MovieService;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        MovieService movieService = new MovieService();

        List<MovieDetailsDTO> movieDetails = movieService.getDanishMoviesLast5Years();
        System.out.println("Fetched " + movieDetails.size() + " movies:");

        movieDetails.forEach(movie -> {
            System.out.println(movie.getTitle() + " (" + movie.getReleaseDate() + ")");
        });
    }
}
