package app.daos;

import app.configs.HibernateConfig;
import app.entities.Genre;
import app.entities.Movie;
import app.populators.TestPopulator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

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
        movieDAO = new MovieDAO(); // DAO methods should now accept EntityManager
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
        }
        populator.populateAll();
    }

    @AfterAll
    void tearDown() {
        if (emf != null && emf.isOpen()) emf.close();
    }

    @Test
    void create() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Movie newMovie = new Movie();
        newMovie.setTitle("New Movie");

        Movie created = movieDAO.create(newMovie, em);

        em.getTransaction().commit();
        em.getTransaction().begin();

        List<Movie> all = movieDAO.getAll(em);

        em.getTransaction().commit();
        em.close();

        assertNotNull(created.getId());
        assertEquals("New Movie", created.getTitle());
        assertEquals(3, all.size(), "Total movies should be 3 (2 from populator + 1 new)");
    }

    @Test
    void getAll() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        List<Movie> movies = movieDAO.getAll(em);

        em.getTransaction().commit();
        em.close();

        assertNotNull(movies);
        assertTrue(movies.size() >= 1, "There should be at least one movie from the populator");
    }

    @Test
    void getById() {
        Movie sample = populator.getSampleMovies().get(1);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Movie found = movieDAO.getById(sample.getId(), em);

        em.getTransaction().commit();
        em.close();

        assertNotNull(found);
        assertEquals(sample.getTitle(), found.getTitle());
    }

    @Test
    void update() {
        Movie movie = populator.getSampleMovies().get(0);
        movie.setTitle("Updated Movie");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Movie updated = movieDAO.update(movie, em);

        em.getTransaction().commit();
        em.getTransaction().begin();

        Movie fetched = movieDAO.getById(movie.getId(), em);

        em.getTransaction().commit();
        em.close();

        assertEquals("Updated Movie", updated.getTitle());
        assertEquals("Updated Movie", fetched.getTitle());
    }

    @Test
    void delete() {
        Movie movie = populator.getSampleMovies().get(0);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        boolean deleted = movieDAO.delete(movie.getId(), em);

        em.getTransaction().commit();
        em.getTransaction().begin();

        Movie shouldBeNull = movieDAO.getById(movie.getId(), em);

        em.getTransaction().commit();
        em.close();

        assertTrue(deleted);
        assertNull(shouldBeNull);
    }

    @Test
    void getMoviesByGenre() {
        Genre genre = populator.getSampleGenres().get(0);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        List<Movie> movies = movieDAO.getMoviesByGenre(genre.getName(), em);

        em.getTransaction().commit();
        em.close();

        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        assertTrue(movies.stream().anyMatch(m -> m.getGenres().contains(genre)));
    }

    @Test
    void getMoviesByTitle() {
        String title = populator.getSampleMovies().get(1).getTitle();

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        List<Movie> movies = movieDAO.getMoviesByTitle(title, em);

        em.getTransaction().commit();
        em.close();

        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        assertTrue(movies.stream().allMatch(m -> m.getTitle().contains(title)));
    }

    @Test
    void getTotalRatingForAllMovies() {
        Movie movie1 = new Movie();
        movie1.setRating(5.5);
        Movie movie2 = new Movie();
        movie2.setRating(6.5);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        movieDAO.create(movie1, em);
        movieDAO.create(movie2, em);
        em.getTransaction().commit();

        em.getTransaction().begin();
        Double totalRating = movieDAO.getTotalRatingForAllMovies(em);
        em.getTransaction().commit();
        em.close();

        assertNotNull(totalRating);
        assertTrue(totalRating >= 0);
    }

    @Test
    void getHighestRatedMovies() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        List<Movie> movies = movieDAO.getHighestRatedMovies(em);

        em.getTransaction().commit();
        em.close();

        assertNotNull(movies);
        assertFalse(movies.isEmpty());
    }

    @Test
    void getLowestRatedMovies() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        List<Movie> movies = movieDAO.getLowestRatedMovies(em);

        em.getTransaction().commit();
        em.close();

        assertNotNull(movies);
    }

    @Test
    void getMostPopularMovies() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        List<Movie> movies = movieDAO.getMostPopularMovies(em);

        em.getTransaction().commit();
        em.close();

        assertNotNull(movies);
        assertFalse(movies.isEmpty());
    }
}
