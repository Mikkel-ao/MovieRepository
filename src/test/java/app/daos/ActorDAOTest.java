package app.daos;

import app.configs.HibernateConfig;
import app.entities.Actor;
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
        actorDAO = new ActorDAO(emf);
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
        Actor newActor = new Actor();
        newActor.setName("New Actor");

        Actor created = actorDAO.create(newActor);

        assertNotNull(created.getId(), "Created actor should have an ID");
        assertEquals("New Actor", created.getName());
    }

    @Test
    void getAll() {
        List<Actor> actors = actorDAO.getAll();
        assertNotNull(actors);
        assertTrue(actors.size() >= 2, "There should be at least the actors from the populator");
    }

    @Test
    void getById() {
        Actor sample = populator.getSampleActors().get(0);
        Actor found = actorDAO.getById(sample.getId());

        assertNotNull(found);
        assertEquals(sample.getName(), found.getName());
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }

}
