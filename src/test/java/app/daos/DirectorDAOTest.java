package app.daos;

import app.configs.HibernateConfig;
import app.populators.TestPopulator;
import app.entities.Director;
import app.entities.Movie;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
/*
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DirectorDAOTest {

    private EntityManagerFactory emf;
    private DirectorDAO directorDAO;
    private TestPopulator populator;

    @BeforeAll
    void initOnce() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        directorDAO = new DirectorDAO(emf);
        populator = new TestPopulator(emf);
    }

    @BeforeEach
    void setUp() {
        try (var em = emf.createEntityManager()) {
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
        // Arrange
        Director newDirector = new Director();
        newDirector.setName("New Director");

        // Act
        Director created = directorDAO.create(newDirector);

        // Assert
        assertNotNull(created.getId());
        assertEquals("New Director", created.getName());
    }

    @Test
    void getAll() {
        // Act
        List<Director> directors = directorDAO.getAll();

        // Assert
        assertNotNull(directors);
        assertTrue(directors.size() >= 2);
    }

    @Test
    void getById() {
        // Arrange
        Director sample = populator.getSampleDirectors().get(0);

        // Act
        Director found = directorDAO.getById(sample.getId());

        // Assert
        assertNotNull(found);
        assertEquals(sample.getName(), found.getName());
    }

    @Test
    void update() {
        // Arrange
        Director director = populator.getSampleDirectors().get(0);
        director.setName("Updated Director");

        // Act
        Director updated = directorDAO.update(director);

        // Assert
        assertEquals("Updated Director", updated.getName());
    }

    @Test
    void delete() {
        // Arrange
        Director director = populator.getSampleDirectors().get(0);

        // Act
        boolean deleted = directorDAO.delete(director.getId());

        // Assert
        assertTrue(deleted);
        assertNull(directorDAO.getById(director.getId()));
    }

    @Test
    void getDirectorByMovieId() {
        // Arrange
        Movie movie = populator.getSampleMovies().get(0);

        // Act
        Director director = directorDAO.getDirectorByMovieId(movie.getId());

        // Assert
        assertNotNull(director);
        assertEquals(movie.getDirector().getId(), director.getId());
    }
}
