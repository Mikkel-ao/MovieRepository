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
    void getAll() {
        // Arrange: actors already populated

        // Act
        List<Actor> actors = actorDAO.getAll();

        // Assert
        assertNotNull(actors);
        assertTrue(actors.size() >= 2, "There should be at least the actors from the populator");
    }

    @Test
    void getById() {
        // Arrange
        Actor sample = populator.getSampleActors().get(0);

        // Act
        Actor found = actorDAO.getById(sample.getId());

        // Assert
        assertNotNull(found);
        assertEquals(sample.getName(), found.getName());
    }

    @Test
    void update() {
        // Arrange
        Actor actor = populator.getSampleActors().get(0);
        actor.setName("Updated Actor");

        // Act
        Actor updated = actorDAO.update(actor);

        // Assert
        assertEquals("Updated Actor", updated.getName(), "Actor name should be updated");

        // Additional Assert: verify persisted in DB
        Actor fetched = actorDAO.getById(actor.getId());
        assertEquals("Updated Actor", fetched.getName(), "Fetched actor should have updated name");
    }

    @Test
    void delete() {
        // Arrange
        Actor actor = populator.getSampleActors().get(0);

        // Act
        boolean deleted = actorDAO.delete(actor.getId());

        // Assert
        assertTrue(deleted, "Actor should be deleted");
        assertNull(actorDAO.getById(actor.getId()), "Deleted actor should not be found in DB");
    }

    @Test
    void getActorsByMovieId() {
        // Arrange
        Movie movie = populator.getSampleMovies().get(0);

        // Act
        List<Actor> actors = actorDAO.getActorsByMovieId(movie.getId());

        // Assert
        assertNotNull(actors, "Actors list should not be null");
        assertFalse(actors.isEmpty(), "Movie should have actors");
        assertTrue(actors.stream()
                        .anyMatch(a -> a.getId() == populator.getSampleActors().get(0).getId()),
                "At least one actor should match the sample actor");
    }

}
