package com.dacaspex.provider.themoviedb;

import com.dacaspex.provider.themoviedb.model.Genre;
import com.dacaspex.provider.themoviedb.model.Movie;
import com.dacaspex.provider.themoviedb.model.Paginated;
import com.google.gson.JsonObject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class JsonMapper {
    public Paginated<Movie> toPaginatedMovies(JsonObject json, Map<Integer, Genre> genreMap) {
        List<Movie> movies = new ArrayList<>();
        json.get("results").getAsJsonArray().forEach(e -> movies.add(toMovie(e.getAsJsonObject(), genreMap)));

        return new Paginated<>(
            movies,
            json.get("page").getAsInt(),
            json.get("total_pages").getAsInt(),
            json.get("total_results").getAsInt()
        );
    }

    public Movie toMovie(JsonObject json, Map<Integer, Genre> genreMap) {
        List<Genre> genres = new ArrayList<>();
        json.get("genre_ids").getAsJsonArray().forEach(e -> {
            genres.add(genreMap.get(e.getAsInt()));
        });

        DateTimeFormatter fmt = DateTimeFormat.forPattern("yyyy-MM-dd");
        DateTime releaseDate = fmt.parseDateTime(json.get("release_date").getAsString());

        return new Movie(
            json.get("id").getAsLong(),
            json.get("title").getAsString(),
            json.get("overview").getAsString(),
            releaseDate,
            genres,
            json.get("vote_average").getAsFloat(),
            json.get("vote_count").getAsLong(),
            json.get("popularity").getAsFloat(),
            json.get("original_language").getAsString(),
            json.get("original_title").getAsString(),
            json.get("adult").getAsBoolean()
        );
    }

    public Genre toGenre(JsonObject json) {
        return new Genre(
            json.get("id").getAsInt(),
            json.get("name").getAsString()
        );
    }
}
