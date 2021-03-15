package com.dacaspex.provider.reddit;

public enum TimePeriod {
    HOURLY,
    DAILY,
    WEEKLY,
    MONTHLY,
    YEARLY,
    ALL_TIME;

    public static TimePeriod fromString(String string) {
        switch (string.toLowerCase()) {
            case "hourly":
                return TimePeriod.HOURLY;
            case "daily":
                return TimePeriod.DAILY;
            case "weekly":
                return TimePeriod.WEEKLY;
            case "monthly":
                return TimePeriod.MONTHLY;
            case "yearly":
                return TimePeriod.YEARLY;
            case "all_time":
                return TimePeriod.ALL_TIME;
            default:
                throw new IllegalArgumentException(
                    String.format("Could not map %s to a valid TimePeriod instance", string)
                );
        }
    }
}
