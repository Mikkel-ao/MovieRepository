package app.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class MovieService {



    private static final String BASE_URL = "https://api.themoviedb.org/3/find/";
    private static final String SEARCH_URL = "https://api.themoviedb.org/3/search/movie";
    private static final String DISCOVER_URL = "https://api.themoviedb.org/3/discover/movie";

    private final ObjectMapper objectMapper = new ObjectMapper();

    public MovieResponseDTO getMovieInfoByImdbId(String imdbId) {
        objectMapper.registerModule(new JavaTimeModule());
        MovieResponseDTO movieResponse = null;

        try {
            String apiKey = System.getenv("API_KEY");
            if (apiKey == null) {
                throw new RuntimeException("API_KEY not found in environment variables!");
            }

            HttpClient client = HttpClient.newHttpClient();

            String url = BASE_URL + imdbId + "?api_key=" + apiKey + "&external_source=imdb_id";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String json = response.body();
                movieResponse = objectMapper.readValue(json, MovieResponseDTO.class);
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movieResponse;
    }

    public MovieResponseDTO getMovieInfoByTitle(String title) {
        objectMapper.registerModule(new JavaTimeModule());
        MovieResponseDTO movieResponse = null;

        try {
            String apiKey = System.getenv("API_KEY");
            if (apiKey == null) {
                throw new RuntimeException("API_KEY not found in environment variables!");
            }

            HttpClient client = HttpClient.newHttpClient();

            String safeTitle = title.replace(" ", "%20");

            String url = SEARCH_URL
                    + "?api_key=" + apiKey
                    + "&query=" + safeTitle;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String json = response.body();
                movieResponse = objectMapper.readValue(json, MovieResponseDTO.class);
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movieResponse;
    }

    public List<MovieInfoDTO> getMoviesByRating(double minRating, double maxRating) {
        objectMapper.registerModule(new JavaTimeModule());
        List<MovieInfoDTO> movieInfoDTOList = new ArrayList<>();

        try {
            String apiKey = System.getenv("API_KEY");
            if (apiKey == null) {
                throw new RuntimeException("API_KEY not found in environment variables!");
            }

            HttpClient client = HttpClient.newHttpClient();

            String url = DISCOVER_URL
                    + "?api_key=" + apiKey
                    + "&vote_average.gte=" + minRating
                    + "&vote_average.lte=" + maxRating
                    + "&page=1";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String json = response.body();
                MovieResponseDTO movieResponse = objectMapper.readValue(json, MovieResponseDTO.class);

                if (movieResponse != null && movieResponse.getMovies() != null) {
                    movieInfoDTOList.addAll(movieResponse.getMovies());
                }
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movieInfoDTOList;
    }



    public List<MovieInfoDTO> getMoviesByQuery(String query) {
        objectMapper.registerModule(new JavaTimeModule());
        List<MovieInfoDTO> movies = new ArrayList<>();
        try {
            String apiKey = System.getenv("API_KEY");
            if (apiKey == null) {
                throw new RuntimeException("API_KEY not found in environment variables!");
            }

            HttpClient client = HttpClient.newHttpClient();
            String safeQuery = query.replace(" ", "%20");

            String url = SEARCH_URL
                    + "?api_key=" + apiKey
                    + "&query=" + safeQuery
                    + "&page=1";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                MovieResponseDTO movieResponse = objectMapper.readValue(response.body(), MovieResponseDTO.class);
                if (movieResponse != null && movieResponse.getMovies() != null) {
                    movies.addAll(movieResponse.getMovies());
                }
            } else {
                System.out.println("GET request failed. Status code: " + response.statusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return movies;
    }

    // Sort movies by release date descending
    public List<MovieInfoDTO> sortMoviesByReleaseDateDesc(List<MovieInfoDTO> movies) {
        if (movies == null) return List.of();

        return movies.stream()
                .sorted(Comparator.comparing(MovieInfoDTO::getReleaseDate,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .collect(Collectors.toList());
    }

    // Convenience method
    public List<MovieInfoDTO> getSortedByReleaseDate(String query) {
        List<MovieInfoDTO> movies = getMoviesByQuery(query);
        return sortMoviesByReleaseDateDesc(movies);
    }
}
