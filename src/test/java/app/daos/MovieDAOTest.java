package app.daos;

import app.configs.HibernateConfig;
import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import app.populators.TestPopulator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MovieDAOTest {

    private EntityManagerFactory emf;
    private MovieDAO movieDAO;
    private TestPopulator populator;

    @BeforeAll
    void initOnce() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        movieDAO = new MovieDAO(emf);
        populator = new TestPopulator(emf);
    }

    @BeforeEach
    void setUp() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createNativeQuery(
                    "TRUNCATE TABLE movie_actor, actor, movie, director, genre RESTART IDENTITY CASCADE"
            ).executeUpdate();
            em.getTransaction().commit();
        } catch (Exception e) {
            throw new RuntimeException("Failed to truncate tables", e);
        }

        // Populate all entities for tests
        populator.populateAll();
    }

    @AfterAll
    void tearDown() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    void create() {
        Movie newMovie = new Movie();
        newMovie.setTitle("New Movie");
        // Act
        Movie created = movieDAO.create(newMovie);
        // Assert
        assertNotNull(created.getId());
        assertEquals("New Movie", created.getTitle());

        // And the total count should be 3 (2 from populator + 1 new)
        List<Movie> all = movieDAO.getAll();
        assertEquals(3, all.size());
    }




    @Test
    void getAll() {
        // Arrange: movies already populated

        // Act
        List<Movie> movies = movieDAO.getAll();

        // Assert
        assertNotNull(movies);
        assertTrue(movies.size() >= 1, "There should be at least one movie from the populator");
    }

    @Test
    void update() {
        // Arrange
        Movie movie = populator.getSampleMovies().get(0);
        movie.setTitle("Updated Movie");

        // Act
        Movie updated = movieDAO.update(movie);

        // Assert
        assertEquals("Updated Movie", updated.getTitle(), "Movie title should be updated");

        // Additional Assert: verify persisted in DB
        Movie fetched = movieDAO.getById(movie.getId());
        assertEquals("Updated Movie", fetched.getTitle(), "Fetched movie should have updated title");
    }

    @Test
    void getById() {
        // Arrange
        Movie sample = populator.getSampleMovies().get(1);

        // Act
        Movie found = movieDAO.getById(sample.getId());

        // Assert
        assertNotNull(found);
        assertEquals(sample.getTitle(), found.getTitle());
    }

    @Test
    void delete() {
        // Arrange
        Movie movie = populator.getSampleMovies().get(0);

        // Act
        boolean deleted = movieDAO.delete(movie.getId());

        // Assert
        assertTrue(deleted, "Movie should be deleted");
        assertNull(movieDAO.getById(movie.getId()), "Deleted movie should not be found in DB");
    }

    @Test
    void getMoviesByGenre() {
        // Arrange
        var genre = populator.getSampleGenres().get(0);

        // Act
        List<Movie> movies = movieDAO.getMoviesByGenre(genre.getName());

        // Assert
        assertNotNull(movies);
        assertFalse(movies.isEmpty(), "There should be movies for the genre");
        assertTrue(movies.stream().anyMatch(m -> m.getGenres().contains(genre)));
    }

    @Test
    void getMoviesByTitle() {
        // Arrange
        String title = populator.getSampleMovies().get(1).getTitle();

        // Act
        List<Movie> movies = movieDAO.getMoviesByTitle(title);

        // Assert
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        assertTrue(movies.stream().allMatch(m -> m.getTitle().contains(title)));
    }

    @Test
    void getTotalRatingForAllMovies() {
        // Arrange: ratings already set by populator
        Movie newMovie = new Movie();
        newMovie.setRating(5.5);
        Movie newMovie2 = new Movie();
        newMovie2.setRating(6.5);
        movieDAO.create(newMovie);
        movieDAO.create(newMovie2);

        // Act
        Double totalRating = movieDAO.getTotalRatingForAllMovies();

        // Assert
        assertNotNull(totalRating);
        assertTrue(totalRating >= 0, "Total rating should be non-negative");
    }

    @Test
    void getHighestRatedMovies() {
        // Arrange

        // Act
        List<Movie> movies = movieDAO.getHighestRatedMovies();

        // Assert
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
    }

    @Test
    void getLowestRatedMovies() {
        // Act
        List<Movie> movies = movieDAO.getLowestRatedMovies();

        // Assert
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
    }

    @Test
    void getMostPopularMovies() {
        // Act
        List<Movie> movies = movieDAO.getMostPopularMovies();

        // Assert
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
    }
}
