package com.dacaspex.provider.reddit;

public enum Sort {
    HOT,
    BEST,
    NEW,
    RISING,
    CONTROVERSIAL,
    TOP;

    public static Sort fromString(String name) {
        switch (name.toLowerCase()) {
            case "hot":
                return Sort.HOT;
            case "best":
                return Sort.BEST;
            case "new":
                return Sort.NEW;
            case "rising":
                return Sort.RISING;
            case "controversial":
                return Sort.CONTROVERSIAL;
            case "top":
                return Sort.TOP;
            default:
                throw new IllegalArgumentException(String.format("Could not map %s to a valid Sort instance", name));
        }
    }
}
