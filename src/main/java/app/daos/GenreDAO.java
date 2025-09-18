package app.daos;

import app.entities.Genre;
import app.entities.Movie;
import app.exceptions.ApiException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import java.util.List;

public class GenreDAO implements IDAO<Genre, Integer>{

    public Genre findOrCreateGenre(String name, EntityManager em) {
        Genre genre = em.createQuery("SELECT g FROM Genre g WHERE g.name = :name", Genre.class)
                .setParameter("name", name)
                .getResultStream()
                .findFirst()
                .orElse(null);

        if (genre == null) {
            genre = new Genre();
            genre.setName(name);
            em.persist(genre);
        }

        return genre;
    }

    public Genre create(Genre genre, EntityManager em) {
        em.persist(genre);
        return genre;
    }

    public Genre getById(Integer id, EntityManager em) {
        return em.find(Genre.class, id);
    }

    public List<Genre> getAll(EntityManager em) {
        return em.createQuery("SELECT g FROM Genre g", Genre.class)
                .getResultList();
    }

    public Genre update(Genre genre, EntityManager em) {
        return em.merge(genre);
    }

    public boolean delete(Integer id, EntityManager em) {
        Genre genre = em.find(Genre.class, id);
        if (genre == null) return false;

        for (Movie movie : genre.getMovies()) {
            movie.getGenres().remove(genre);
        }

        em.remove(genre);
        return true;
    }
}
