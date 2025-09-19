package app.daos;

import app.configs.HibernateConfig;
import app.entities.Actor;
import app.entities.Movie;
import app.populators.TestPopulator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ActorDAOTest {

    private EntityManagerFactory emf;
    private ActorDAO actorDAO;
    private TestPopulator populator;

    @BeforeAll
    void initOnce() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        actorDAO = new ActorDAO();
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
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }

    @Test
    void getAll() {
        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        List<Actor> actors = actorDAO.getAll(em);

        em.getTransaction().commit();
        em.close();

        assertNotNull(actors);
        assertTrue(actors.size() >= 2, "There should be at least the actors from the populator");
    }

    @Test
    void getById() {
        Actor sample = populator.getSampleActors().get(0);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();

        Actor found = actorDAO.getById(sample.getId(), em);

        em.getTransaction().commit();
        em.close();

        assertNotNull(found);
        assertEquals(sample.getName(), found.getName());
    }

    @Test
    void update() {
        Actor sample = populator.getSampleActors().get(0);
        sample.setName("Updated Actor");

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        Actor updated = actorDAO.update(sample, em);
        em.getTransaction().commit();

        em.getTransaction().begin();
        Actor fetched = actorDAO.getById(sample.getId(), em);
        em.getTransaction().commit();
        em.close();

        assertEquals("Updated Actor", updated.getName());
        assertEquals("Updated Actor", fetched.getName());
    }

    @Test
    void delete() {
        Actor sample = populator.getSampleActors().get(0);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        boolean deleted = actorDAO.delete(sample.getId(), em);
        em.getTransaction().commit();

        em.getTransaction().begin();
        Actor shouldBeNull = actorDAO.getById(sample.getId(), em);
        em.getTransaction().commit();
        em.close();

        assertTrue(deleted);
        assertNull(shouldBeNull);
    }

    @Test
    void getActorsByMovieId() {
        Movie movie = populator.getSampleMovies().get(0);

        EntityManager em = emf.createEntityManager();
        em.getTransaction().begin();
        List<Actor> actors = actorDAO.getActorsByMovieId(movie.getId(), em);
        em.getTransaction().commit();
        em.close();

        assertNotNull(actors);
        assertFalse(actors.isEmpty(), "Movie should have actors");
        assertTrue(actors.stream().anyMatch(a -> a.getId() == populator.getSampleActors().get(0).getId()));
    }
}
