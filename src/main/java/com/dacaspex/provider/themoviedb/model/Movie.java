package com.dacaspex.provider.themoviedb.model;

import org.joda.time.DateTime;

import java.util.List;

public class Movie {
    private final long id;
    private final String title;
    private final String overview;
    private final DateTime releaseDate;
    private final List<Genre> genres;

    private final float voteAverage;
    private final long voteCount;
    private final float popularity;

    private final String originalLanguage;
    private final String originalTitle;

    private final boolean adult;

    public Movie(
        long id,
        String title,
        String overview,
        DateTime releaseDate,
        List<Genre> genres,
        float voteAverage,
        long voteCount,
        float popularity,
        String originalLanguage,
        String originalTitle,
        boolean adult
    ) {
        this.id = id;
        this.title = title;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.genres = genres;
        this.voteAverage = voteAverage;
        this.voteCount = voteCount;
        this.popularity = popularity;
        this.originalLanguage = originalLanguage;
        this.originalTitle = originalTitle;
        this.adult = adult;
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public DateTime getReleaseDate() {
        return releaseDate;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public float getVoteAverage() {
        return voteAverage;
    }

    public long getVoteCount() {
        return voteCount;
    }

    public float getPopularity() {
        return popularity;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public boolean isAdult() {
        return adult;
    }
}
