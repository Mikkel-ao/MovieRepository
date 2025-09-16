package app.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Data
@EqualsAndHashCode(exclude = {"actors", "genres", "director"})
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;
    private LocalDate releaseDate;
    private Double rating;
    private Double popularity;

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "director_id")
    @ToString.Exclude
    private Director director;

    @ManyToMany(cascade = CascadeType.ALL,  fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_actor",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "actor_id")
    )
    @ToString.Exclude
    private Set<Actor> actors = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "movie_genre",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    @ToString.Exclude
    private Set<Genre> genres = new HashSet<>();

    // --- Helper methods ---
    public void addActor(Actor actor) {
        if (actor != null) {
            actors.add(actor);
            actor.getMovies().add(this);
        }
    }

    public void addGenre(Genre genre) {
        if (genre != null) {
            genres.add(genre);
            //genre.getMovies().add(this);
        }
    }

    public void setDirector(Director director) {
        this.director = director;
        if (director != null) {
            director.getMovies().add(this);
        }
    }
    @Override
    public String toString() {
        return "Movie{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", releaseDate=" + releaseDate +
                ", rating=" + rating +
                ", popularity=" + popularity +
                ", genres=" + genres.stream().map(Genre::getName).toList() +
                '}';
    }
}
