package app.daos;

import app.configs.HibernateConfig;
import app.populators.TestPopulator;
import app.entities.Director;
import app.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DirectorDAOTest {

    private EntityManagerFactory emf;
    private DirectorDAO directorDAO;
    private TestPopulator populator;

    @BeforeAll
    void initOnce() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        directorDAO = new DirectorDAO(); // assuming DAO methods now take EntityManager
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

        Director newDirector = new Director();
        newDirector.setName("New Director");
        Director created = directorDAO.create(newDirector, em);

        em.getTransaction().commit();
        em.close();

        assertNotNull(created.getId());
        assertEquals("New Director", created.getName());
    }

    @Test
    void getAll() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        List<Director> directors = directorDAO.getAll(em);

        em.getTransaction().commit();
        em.close();

        assertNotNull(directors);
        assertTrue(directors.size() >= 2, "There should be at least the directors from the populator");
    }

    @Test
    void getById() {
        Director sample = populator.getSampleDirectors().get(0);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Director found = directorDAO.getById(sample.getId(), em);

        em.getTransaction().commit();
        em.close();

        assertNotNull(found);
        assertEquals(sample.getName(), found.getName());
    }

    @Test
    void update() {
        Director director = populator.getSampleDirectors().get(0);
        director.setName("Updated Director");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Director updated = directorDAO.update(director, em);

        em.getTransaction().commit();
        em.getTransaction().begin();

        Director fetched = directorDAO.getById(director.getId(), em);
        em.getTransaction().commit();
        em.close();

        assertEquals("Updated Director", updated.getName());
        assertEquals("Updated Director", fetched.getName());
    }

    @Test
    void delete() {
        Director director = populator.getSampleDirectors().get(0);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        boolean deleted = directorDAO.delete(director.getId(), em);

        em.getTransaction().commit();
        em.getTransaction().begin();

        Director shouldBeNull = directorDAO.getById(director.getId(), em);
        em.getTransaction().commit();
        em.close();

        assertTrue(deleted);
        assertNull(shouldBeNull);
    }

    @Test
    void getDirectorByMovieId() {
        Movie movie = populator.getSampleMovies().get(0);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Director director = directorDAO.getDirectorByMovieId(movie.getId(), em);

        em.getTransaction().commit();
        em.close();

        assertNotNull(director);
        assertEquals(movie.getDirector().getId(), director.getId());
    }
}
