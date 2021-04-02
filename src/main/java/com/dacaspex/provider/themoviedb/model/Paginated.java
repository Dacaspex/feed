package com.dacaspex.provider.themoviedb.model;

import java.util.List;

public class Paginated<T> {
    private List<T> objects;
    private int page;
    private int totalPages;
    private int totalResults;

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
