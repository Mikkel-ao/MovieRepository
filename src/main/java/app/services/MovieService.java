package app.services;

import app.dtos.MovieDetailsDTO;
import app.dtos.MovieResponse;
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
    private static final int TMDB_PAGE_LIMIT = 100;

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

        // Create the executor service inside the method to ensure it's new for each call
        int cores = Runtime.getRuntime().availableProcessors();
        ExecutorService executor = Executors.newFixedThreadPool(cores);

        try {
            while (page <= TMDB_PAGE_LIMIT) {
                String url = String.format("%s?api_key=%s&language=da-DK&region=DK&with_origin_country=DK&with_original_language=da&primary_release_date.gte=%s&primary_release_date.lte=%s&sort_by=primary_release_date.desc&page=%d", DISCOVER_URL, apiKey, fiveYearsAgo, today, page);

                MovieResponse movieResponse = sendRequest(url, MovieResponse.class);
                if (movieResponse == null || movieResponse.getResults() == null || movieResponse.getResults().isEmpty()) {
                    break; // No more movies or empty response
                }

                List<Future<MovieDetailsDTO>> futures = new ArrayList<>();
                for (MovieDetailsDTO dto : movieResponse.getResults()) {
                    futures.add(executor.submit(() -> getMovieWithCredits(dto.getId())));
                }

                for (Future<MovieDetailsDTO> future : futures) {
                    try {
                        MovieDetailsDTO details = future.get();
                        if (details != null) {
                            movies.add(details);
                        }
                    } catch (ExecutionException | InterruptedException e) {
                        System.err.println("Error fetching movie details: " + e.getMessage());
                        // Consider how to handle this. For now, we continue to the next future.
                    }
                }
                page++;
            }
        } finally {
            executor.shutdown();
        }
        return movies;
    }

    public MovieDetailsDTO getMovieWithCredits(int id) {
        String url = String.format("%s%d?api_key=%s&language=da-DK&append_to_response=credits", MOVIE_BY_ID_URL, id, apiKey);
        return sendRequest(url, MovieDetailsDTO.class);
    }

    private <T> T sendRequest(String url, Class<T> responseType) {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 && response.body() != null) {
                return objectMapper.readValue(response.body(), responseType);
            } else {
                System.out.println("GET request failed for URL: " + url + ". Status: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Error during HTTP request to URL: " + url + ". " + e.getMessage());
        }
        return null;
    }
}