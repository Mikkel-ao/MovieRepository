package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MovieDetailsDTO {
    private int id;
    private String title;

    @JsonProperty("release_date")
    private String releaseDate;

    private double popularity;

    @JsonProperty("vote_average")
    private double rating;

    private List<GenreDTO> genres;
    private CreditsDTO credits;
}





