package app.populators;

import app.entities.Actor;
import app.entities.Director;
import app.entities.Genre;
import app.entities.Movie;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
public class TestPopulator {

    private final EntityManagerFactory emf;

    private List<Actor> sampleActors = new ArrayList<>();
    private List<Movie> sampleMovies = new ArrayList<>();
    private List<Director> sampleDirectors = new ArrayList<>();
    private List<Genre> sampleGenres = new ArrayList<>();

    public TestPopulator(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // Populate Actors
    public void populateActors() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Actor a1 = new Actor();
            a1.setName("Actor 1");

            Actor a2 = new Actor();
            a2.setName("Actor 2");

            Actor a3 = new Actor();
            a3.setName("Actor 3");

            em.persist(a1);
            em.persist(a2);
            em.persist(a3);

            em.getTransaction().commit();

            sampleActors.add(a1);
            sampleActors.add(a2);
            sampleActors.add(a3);
        }
    }

    // Populate Directors
    public void populateDirectors() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Director d1 = new Director();
            d1.setName("Director 1");

            Director d2 = new Director();
            d2.setName("Director 2");

            em.persist(d1);
            em.persist(d2);

            em.getTransaction().commit();

            sampleDirectors.add(d1);
            sampleDirectors.add(d2);
        }
    }

    // Populate Genres
    public void populateGenres() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            Genre g1 = new Genre();
            g1.setName("Action");

            Genre g2 = new Genre();
            g2.setName("Comedy");

            Genre g3 = new Genre();
            g3.setName("Drama");

            em.persist(g1);
            em.persist(g2);
            em.persist(g3);

            em.getTransaction().commit();

            sampleGenres.add(g1);
            sampleGenres.add(g2);
            sampleGenres.add(g3);
        }
    }

    // Populate Movies with Directors, Actors, and Genres
    public void populateMovies() {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Ensure all dependencies exist
            if (sampleDirectors.isEmpty()) populateDirectors();
            if (sampleActors.isEmpty()) populateActors();
            if (sampleGenres.isEmpty()) populateGenres();

            // Movie 1
            Movie m1 = new Movie();
            m1.setTitle("Movie 1");
            m1.setReleaseDate(LocalDate.of(2020, 1, 1));
            m1.setDirector(em.merge(sampleDirectors.get(0)));
            m1.addActor(em.merge(sampleActors.get(0)));
            m1.addActor(em.merge(sampleActors.get(1)));
            m1.addGenre(em.merge(sampleGenres.get(0)));
            m1.addGenre(em.merge(sampleGenres.get(1)));

            // Movie 2
            Movie m2 = new Movie();
            m2.setTitle("Movie 2");
            m2.setReleaseDate(LocalDate.of(2021, 5, 10));
            m2.setDirector(em.merge(sampleDirectors.get(1)));
            m2.addActor(em.merge(sampleActors.get(1)));
            m2.addActor(em.merge(sampleActors.get(2)));
            m2.addGenre(em.merge(sampleGenres.get(2)));

            // Persist movies
            em.persist(m1);
            em.persist(m2);

            em.getTransaction().commit();

            sampleMovies.add(m1);
            sampleMovies.add(m2);
        }
    }

    // Convenience method to populate everything
    public void populateAll() {
        populateActors();
        populateDirectors();
        populateGenres();
        populateMovies();
    }
}
