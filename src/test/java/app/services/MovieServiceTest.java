package app.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
    }

    @Test
    void getMovieWithCredits() {
    }
}