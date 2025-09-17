package app.services;

import app.dtos.MovieDetailsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieServiceTest {
    private MovieService movieService;

    @BeforeEach
    void setUp() {
        assertNotNull(System.getenv("API_KEY"), "Please set TMDb API_KEY in your environment");
        movieService = new MovieService();
    }


    @Test
    void getDanishMoviesLast5Years() {
        // Arrange & Act
        List<MovieDetailsDTO> movies = movieService.getDanishMoviesLast5Years();

        // Assert
        assertNotNull(movies);
        assertTrue(movies.size() >= 1300, "We're expecting a little over 1300 movies");
    }

    @Test
    void getMovieWithCredits() {

    }
}