package com.dacaspex.provider.themoviedb.model;

import java.util.List;

public class Paginated<T> {
    private final List<T> objects;
    private final int page;
    private final int totalPages;
    private final int totalResults;

    public Paginated(
        List<T> objects,
        int page,
        int totalPages,
        int totalResults
    ) {
        this.objects = objects;
        this.page = page;
        this.totalPages = totalPages;
        this.totalResults = totalResults;
    }

    public List<T> getObjects() {
        return objects;
    }

    public int getPage() {
        return page;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalResults() {
        return totalResults;
    }
}
