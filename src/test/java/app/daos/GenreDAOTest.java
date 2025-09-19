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
        genreDAO = new GenreDAO(); // assuming DAO now takes EntityManager in methods
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

        Genre genre = new Genre();
        genre.setName("Sci-Fi");
        Genre created = genreDAO.create(genre, em);

        em.getTransaction().commit();
        em.close();

        assertNotNull(created.getId(), "Created genre should have an ID");
        assertEquals("Sci-Fi", created.getName());
    }

    @Test
    void getAll() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        List<Genre> genres = genreDAO.getAll(em);

        em.getTransaction().commit();
        em.close();

        assertNotNull(genres);
        assertTrue(genres.size() >= 2, "There should be at least the genres from the populator");
    }

    @Test
    void getById() {
        Genre sample = populator.getSampleGenres().get(0);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Genre found = genreDAO.getById(sample.getId(), em);

        em.getTransaction().commit();
        em.close();

        assertNotNull(found);
        assertEquals(sample.getName(), found.getName());
    }

    @Test
    void update() {
        Genre genre = populator.getSampleGenres().get(0);
        genre.setName("Updated Genre");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Genre updated = genreDAO.update(genre, em);

        em.getTransaction().commit();
        em.getTransaction().begin();

        Genre fetched = genreDAO.getById(genre.getId(), em);
        em.getTransaction().commit();
        em.close();

        assertEquals("Updated Genre", updated.getName(), "Genre name should be updated");
        assertEquals("Updated Genre", fetched.getName(), "Fetched genre should have updated name");
    }

    @Test
    void delete() {
        Genre genre = populator.getSampleGenres().get(0);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        boolean deleted = genreDAO.delete(genre.getId(), em);

        em.getTransaction().commit();
        em.getTransaction().begin();

        Genre shouldBeNull = genreDAO.getById(genre.getId(), em);
        em.getTransaction().commit();
        em.close();

        assertTrue(deleted, "Genre should be deleted");
        assertNull(shouldBeNull, "Deleted genre should not be found in DB");
    }

    @Test
    void findOrCreateGenre() {
        String name = "Fantasy";

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Genre genre1 = genreDAO.findOrCreateGenre(name, em);
        Genre genre2 = genreDAO.findOrCreateGenre(name, em);

        em.getTransaction().commit();
        em.close();

        assertNotNull(genre1.getId(), "Genre should have an ID");
        assertEquals(genre1.getId(), genre2.getId(), "Should return the same genre if it already exists");
    }
}
