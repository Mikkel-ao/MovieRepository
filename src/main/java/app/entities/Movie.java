package app.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDate releaseDate;
    private Double rating;
    private Double popularity;

    @ManyToOne
    private Director director;

    @ManyToMany
    @JoinTable(name = "movie_actor")
    private Set<Actor> actors;

    @ManyToMany
    @JoinTable(name = "movie_genre")
    private Set<Genre> genres;
}