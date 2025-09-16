package app.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DirectorDTO {
    private int id;
    private String name;
    @JsonProperty("department")
    private String department;
    @JsonProperty("job")
    private String job;
}
