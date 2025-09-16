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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MovieService {

    private static final String DISCOVER_URL = "https://api.themoviedb.org/3/discover/movie";
    private static final String MOVIE_BY_ID_URL = "https://api.themoviedb.org/3/movie/";

    private final ObjectMapper objectMapper;
    private final HttpClient client;
    private final String apiKey;
    private final ExecutorService executor;

    public MovieService() {
        this.objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        this.client = HttpClient.newHttpClient();
        this.apiKey = System.getenv("API_KEY");
        if (apiKey == null) {
            throw new RuntimeException("API_KEY not found in environment variables!");
        }
        this.executor = Executors.newFixedThreadPool(13);
    }

    public List<MovieDetailsDTO> getDanishMoviesLast5Years() {
        List<MovieDetailsDTO> movies = new ArrayList<>();
        LocalDate today = LocalDate.now();
        LocalDate fiveYearsAgo = today.minusYears(5);

        int page = 1;
        boolean morePages = true;

        while (morePages) {
            try {
                String url = DISCOVER_URL + "?api_key=" + apiKey + "&language=da-DK" + "&region=DK" + "&with_origin_country=DK" + "&primary_release_date.gte=" + fiveYearsAgo + "&primary_release_date.lte=" + today + "&sort_by=primary_release_date.desc" + "&page=" + page;


                HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200 && response.body() != null) {
                    MovieResponse movieResponse = objectMapper.readValue(response.body(), MovieResponse.class);
                    if (movieResponse.getResults() != null && !movieResponse.getResults().isEmpty()) {
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
                            } catch (Exception e) {
                                System.err.println("Error fetching movie details: " + e.getMessage());
                            }
                        }
                        page++;
                        if (page > 100) morePages = false; // TMDB page limit
                    } else {
                        morePages = false;
                    }
                } else {
                    System.out.println("GET request failed. Status code: " + response.statusCode());
                    morePages = false;
                }

            } catch (Exception e) {
                System.err.println("Error fetching Danish movies: " + e.getMessage());
                e.printStackTrace();
                morePages = false;
            }
        }
        executor.shutdown();
        return movies;
    }


    public MovieDetailsDTO getMovieWithCredits(int id) {
        try {
            String url = MOVIE_BY_ID_URL + id + "?api_key=" + apiKey + "&language=da-DK" + "&append_to_response=credits";

            HttpRequest request = HttpRequest.newBuilder().uri(new URI(url)).GET().build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 && response.body() != null) {
                return objectMapper.readValue(response.body(), MovieDetailsDTO.class);
            } else {
                System.out.println("GET request failed for movie id " + id + ". Status: " + response.statusCode());
            }
        } catch (Exception e) {
            System.err.println("Error fetching movie with credits for ID " + id + ": " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}
