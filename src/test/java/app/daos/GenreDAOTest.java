package app.daos;

import app.configs.HibernateConfig;
import app.entities.Genre;
import app.populators.TestPopulator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GenreDAOTest {

    private EntityManagerFactory emf;
    private GenreDAO genreDAO;
    private TestPopulator populator;

    @BeforeAll
    void initOnce() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        genreDAO = new GenreDAO(emf);
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
        // Arrange
        Genre genre = new Genre();
        genre.setName("Sci-Fi");

        // Act
        Genre created = genreDAO.create(genre);

        // Assert
        assertNotNull(created.getId(), "Created genre should have an ID");
        assertEquals("Sci-Fi", created.getName());
    }

    @Test
    void getAll() {
        // Arrange: genres already populated

        // Act
        List<Genre> genres = genreDAO.getAll();

        // Assert
        assertNotNull(genres);
        assertTrue(genres.size() >= 2, "There should be at least the genres from the populator");
    }

    @Test
    void getById() {
        // Arrange
        Genre sample = populator.getSampleGenres().get(0);

        // Act
        Genre found = genreDAO.getById(sample.getId());

        // Assert
        assertNotNull(found);
        assertEquals(sample.getName(), found.getName());
    }

    @Test
    void update() {
        // Arrange
        Genre genre = populator.getSampleGenres().get(0);
        genre.setName("Updated Genre");

        // Act
        Genre updated = genreDAO.update(genre);

        // Assert
        assertEquals("Updated Genre", updated.getName(), "Genre name should be updated");

        // Additional Assert: verify persisted in DB
        Genre fetched = genreDAO.getById(genre.getId());
        assertEquals("Updated Genre", fetched.getName(), "Fetched genre should have updated name");
    }

    @Test
    void delete() {
        // Arrange
        Genre genre = populator.getSampleGenres().get(0);

        // Act
        boolean deleted = genreDAO.delete(genre.getId());

        // Assert
        assertTrue(deleted, "Genre should be deleted");
        assertNull(genreDAO.getById(genre.getId()), "Deleted genre should not be found in DB");
    }

    @Test
    void findOrCreateGenre() {
        // Arrange
        String name = "Fantasy";

        // Act
        Genre genre1 = genreDAO.findOrCreateGenre(name);
        Genre genre2 = genreDAO.findOrCreateGenre(name);

        // Assert
        assertNotNull(genre1.getId(), "Genre should have an ID");
        assertEquals(genre1.getId(), genre2.getId(), "Should return the same genre if it already exists");
    }
}
