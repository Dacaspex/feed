package com.dacaspex.provider.themoviedb.condition;

import com.dacaspex.provider.condition.Condition;
import com.dacaspex.provider.themoviedb.model.Movie;

import java.util.List;

/**
 * The conditions holds if the movie contains all the given genres to this condition. More formally:
 * this conditions is true iff the intersection of the movie genres and the given genres is equal to
 * the given genres.
 */
public class HasGenresCondition implements Condition<Movie> {
    private final List<String> genres;

    public HasGenresCondition(List<String> genres) {
        this.genres = genres;
    }

    @Override
    public boolean holds(Movie value) {
        return genres
            .stream()
            .allMatch(name ->
                // Check whether the genre name occurs in the movie genres
                value.getGenres()
                    .stream()
                    .anyMatch(genre -> genre.getName().equalsIgnoreCase(name))
            );
    }
}
