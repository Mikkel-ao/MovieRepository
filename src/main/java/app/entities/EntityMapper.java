package app.entities;

import app.dtos.ActorDTO;
import app.dtos.DirectorDTO;
import app.dtos.GenreDTO;
import app.dtos.MovieDetailsDTO;

import java.time.LocalDate;
import java.util.List;


public class EntityMapper {
    public List<Actor> convertToActor(List<ActorDTO> dtoList) {
        return dtoList.stream()
                .map(dto -> {
                    Actor actor = new Actor();
                    actor.setId(dto.getId());
                    actor.setName(dto.getName());
                    return actor;
                })
                .toList();
    }

    public List<Director> convertToDirector(List<DirectorDTO> dtoList) {
        return dtoList.stream()
                .map(dto -> {
                    Director director = new Director();
                    director.setId(dto.getId());
                    director.setName(dto.getName());
                    return director;
                })
                .toList();
    }

    public List<Genre> convertToGenre(List<GenreDTO> dtoList) {
        return dtoList.stream()
                .map(dto -> {
                    Genre genre = new Genre();
                    genre.setId(dto.getId());
                    genre.setName(dto.getName());
                    return genre;
                })
                .toList();
    }

    public List<Movie> convertToMovie(List<MovieDetailsDTO> dtoList) {
        return dtoList.stream()
                .map(dto -> {
                    Movie movie = new Movie();
                    movie.setId(dto.getId());
                    movie.setTitle(dto.getTitle());
                    movie.setReleaseDate(dto.getLocalReleaseDate());
                    movie.setRating(dto.getRating());
                    movie.setPopularity(dto.getPopularity());
                    return movie;
                })
                .toList();
    }
}


