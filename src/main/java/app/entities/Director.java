package app.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @OneToMany(mappedBy = "director",fetch = FetchType.EAGER)
    @ToString.Exclude
    private Set<Movie> movies = new HashSet<>();
}
