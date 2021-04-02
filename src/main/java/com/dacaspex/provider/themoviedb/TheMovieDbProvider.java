package com.dacaspex.provider.themoviedb;

import com.dacaspex.provider.Provider;
import com.dacaspex.provider.RunnableType;
import com.dacaspex.provider.condition.Conditions;
import com.dacaspex.provider.themoviedb.exception.EmptyBodyException;
import com.dacaspex.provider.themoviedb.model.Genre;
import com.dacaspex.provider.themoviedb.model.Movie;
import com.dacaspex.provider.themoviedb.model.Paginated;
import com.dacaspex.storage.event.EventStorage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TheMovieDbProvider implements Provider {
    private final static String TMDB_MOVIE_BASE_URL = "https://www.themoviedb.org/movie";
    private final static Logger logger = LogManager.getLogger();

    private final String name;
    private final String source;
    private final String apiKey;
    private final int interval;

    private final JsonMapper jsonMapper;
    private final EventStorage eventStorage;
    private final Conditions<Movie> conditions;

    public TheMovieDbProvider(
        String name,
        String source,
        String apiKey,
        int interval,
        EventStorage eventStorage,
        Conditions<Movie> conditions
    ) {
        this.name = name;
        this.source = source;
        this.apiKey = apiKey;
        this.interval = interval;
        this.jsonMapper = new JsonMapper();
        this.eventStorage = eventStorage;
        this.conditions = conditions;
    }

    @Override
    public RunnableType getRunnableType() {
        return RunnableType.ANYTIME;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void execute() {
        OkHttpClient client = new OkHttpClient();
        DateTime now = new DateTime();
        DateTime later = now.plusDays(interval);

        // Get all upcoming movies
        List<Movie> movies;
        try {
            movies = getMovies(client, apiKey, now, later);
        } catch (EmptyBodyException | IOException e) {
            logger.error(e);
            return;
        }

        for (Movie movie : movies) {
            // The TMDB API also gives movies that are released before today. They might
            // be "re-released", or coming out on another platform? We filter those out explicitly
            if (movie.getReleaseDate().isBefore(now)) {
                continue;
            }

            if (!conditions.holds(movie)) {
                continue;
            }

            eventStorage.insertOrUpdateCalendarEvent(
                Long.toString(movie.getId()),
                source,
                movie.getReleaseDate(),
                movie.getTitle(),
                buildMovieLink(movie)
            );
        }
    }

    /**
     * Gets all upcoming movies. The API is paginated, this method takes care of that
     */
    private List<Movie> getMovies(
        OkHttpClient client,
        String apiKey,
        DateTime releaseDateStart,
        DateTime releaseDateEnd
    ) throws EmptyBodyException, IOException {
        // The API is paginated, so we iterate over each page
        List<Movie> movies = new ArrayList<>();
        int page = 1;
        int totalPages;

        do {
            // Get all genres, because the upcoming movies endpoint only gives genre ids
            List<Genre> genres = getGenres(client, apiKey);
            Map<Integer, Genre> genreMap = new HashMap<>();
            for (Genre genre : genres) {
                genreMap.put(genre.getId(), genre);
            }

            // Get and collect next page of movies
            JsonObject json = execute(client, buildUpcomingMoviesUrl(apiKey, releaseDateStart, releaseDateEnd, page));
            Paginated<Movie> paginatedMovies = jsonMapper.toPaginatedMovies(json, genreMap);
            movies.addAll(paginatedMovies.getObjects());

            // Bookkeeping for pagination
            page = paginatedMovies.getPage() + 1;
            totalPages = paginatedMovies.getTotalPages();
        } while (page < totalPages);

        return movies;
    }

    /**
     * Get the list of genres, which is actually a very tiny list. We need to do this
     * because the movies only come with the genre ids
     */
    private List<Genre> getGenres(OkHttpClient client, String apiKey) throws IOException, EmptyBodyException {
        JsonObject json = execute(client, buildGenresUrl(apiKey));

        List<Genre> genres = new ArrayList<>();
        json.get("genres").getAsJsonArray().forEach(e -> genres.add(jsonMapper.toGenre(e.getAsJsonObject())));

        return genres;
    }

    /**
     * Executes a HTTP request expecting json
     */
    private JsonObject execute(OkHttpClient client, String url) throws EmptyBodyException, IOException {
        Request request = new Request.Builder()
            .url(url)
            .build();

        Response response = client.newCall(request).execute();

        // Attempt to read and parse the response
        ResponseBody body = response.body();
        if (body == null) {
            throw new EmptyBodyException();
        }

        Gson gson = new Gson();
        String content = body.string();

        return gson.fromJson(content, JsonObject.class);
    }

    /**
     * Builds the API URL to get the list of genres
     */
    private String buildGenresUrl(String apiKey) {
        return String.format(
            "https://api.themoviedb.org/3/genre/movie/list" +
                "?api_key=%s",
            apiKey
        );
    }

    /**
     * Builds the API URL to get upcoming movies
     */
    private String buildUpcomingMoviesUrl(String apiKey, DateTime releaseDateStart, DateTime releaseDateEnd, int page) {
        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");

        return String.format(
            "https://api.themoviedb.org/3/discover/movie" +
                "?api_key=%s" +
                "&language=en-US" +
                "&sort_by=primary_release_date.asc" +
                "&include_adult=false" +
                "&include_video=false" +
                "&release_date.gte=%s" +
                "&release_date.lte=%s" +
                "&with_release_type=1|3" +
                "&with_original_language=en" +
                "&page=%s",
            apiKey,
            fmt.print(releaseDateStart),
            fmt.print(releaseDateEnd),
            page
        );
    }

    /**
     * Builds a URL that points to the page of the movie
     */
    private String buildMovieLink(Movie movie) {
        return String.format("%s/%s", TMDB_MOVIE_BASE_URL, movie.getId());
    }
}
