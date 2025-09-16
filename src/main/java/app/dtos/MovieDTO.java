package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDTO {
    @JsonProperty("id")
    private int id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("popularity")
    private double popularity;
    @JsonProperty("release_date")
    private LocalDate releaseDate;
    @JsonProperty("vote_average")
    private double voteAverage;
    @JsonProperty("original_language")
    private String originalLanguage;
    @JsonProperty("genre_ids")
    private List<GenreDTO> genres;
}
