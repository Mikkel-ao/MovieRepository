package app.services;

import app.dtos.MovieDetailsDTO;
import app.dtos.MovieResponse;
import app.exceptions.ApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class MovieService {

    private static final String DISCOVER_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String MOVIE_BY_ID_URL = "https://api.themoviedb.org/3/movie/";
    private static final int API_PAGE_LIMIT = 100;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private final HttpClient client = HttpClient.newHttpClient();
    private final String apiKey;

    public MovieService() {
        this.apiKey = System.getenv("API_KEY");
        if (apiKey == null) {
            throw new RuntimeException("API_KEY not found in environment variables!");
        }
    }

    public List<MovieDetailsDTO> getDanishMoviesLast5Years() {
        List<MovieDetailsDTO> movies = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate fiveYearsAgo = today.minusYears(5);
        int page = 1;

        while (page <= API_PAGE_LIMIT) { // TMDB API page limit
            String url = String.format("%s?api_key=%s&language=da-DK&with_origin_country=DK&with_original_language=da&primary_release_date.gte=%s&primary_release_date.lte=%s&sort_by=primary_release_date.desc&page=%d", DISCOVER_URL, apiKey, fiveYearsAgo, today, page);

            MovieResponse response = sendRequest(url, MovieResponse.class, "Failed to fetch movie list");
            if (response.getResults() == null || response.getResults().isEmpty()) break;

            // Concurrently fetch movie details using default ForkJoinPool
            List<CompletableFuture<MovieDetailsDTO>> futures = response.getResults().stream()
                    .map(dto -> CompletableFuture.supplyAsync(() -> getMovieWithCredits(dto.getId())))
                    .toList();

            movies.addAll(futures.stream()
                    .map(CompletableFuture::join) // waits for completion
                    .toList());

            page++;
        }
        return movies;
    }

    public MovieDetailsDTO getMovieWithCredits(int id) {
        String url = String.format("%s%d?api_key=%s&language=da-DK&append_to_response=credits", MOVIE_BY_ID_URL, id, apiKey);
        return sendRequest(url, MovieDetailsDTO.class, "Failed to fetch movie details");
    }

    private <T> T sendRequest(String url, Class<T> responseType, String errorMessage) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url)).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 && response.body() != null) {
                return objectMapper.readValue(response.body(), responseType);
            } else {
                throw new ApiException(response.statusCode(), errorMessage + ": " + response.body());
            }
        } catch (Exception e) {
            throw new ApiException(500, errorMessage + ": " + e.getMessage());
        }
    }
}