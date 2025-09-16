package app.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Data
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String title;

    private LocalDate releaseDate;
    private Double rating;
    private Double popularity;

    @ManyToOne(cascade = CascadeType.ALL)
    private Director director;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Actor> actors;

    @ManyToMany(cascade = CascadeType.ALL)
    private Set<Genre> genres;
}