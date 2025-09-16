package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseDTO {
    @JsonProperty("movie_results")
    private List<MovieDTO> movies;
    @JsonProperty("results")
    private List<ActorDTO> actors;
    @JsonProperty("crew")
    private List<DirectorDTO> directors;
    @JsonProperty("genres")
    private List<GenreDTO> genres;
}
