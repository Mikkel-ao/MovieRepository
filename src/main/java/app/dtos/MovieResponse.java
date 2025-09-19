package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieResponse {
    @JsonProperty("results")
    private List<MovieDetailsDTO> results;

    @JsonProperty("movie_results")
    private void unpackMovieResults(List<MovieDetailsDTO> movieResults) {
        if (movieResults != null) {
            this.results = movieResults;
        }
    }
}

