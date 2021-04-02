package com.dacaspex.provider.themoviedb;

import com.dacaspex.provider.themoviedb.model.Movie;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

/**
 * A movie is accepted iff it contains a genre in the accepted genres list
 */
public class GenreFilter implements Predicate<Movie> {
    private final List<String> acceptedGenreNames;
    private final Map<Integer, String> genreMap;

    public GenreFilter(List<String> acceptedGenreNames, Map<Integer, String> genreMap) {
        this.acceptedGenreNames = acceptedGenreNames;
        this.genreMap = genreMap;
    }

    @Override
    public boolean test(Movie movie) {
        // At least one genre of the movie should occur in the accepted genres list to
        // pass the test
        return movie.getGenres()
            .stream()
            .anyMatch(id -> acceptedGenreNames.contains(genreMap.get(id)));
    }
}
