package com.dacaspex.provider.reddit;

import com.google.gson.annotations.SerializedName;

public enum TimePeriod {
    @SerializedName("hourly")
    HOURLY,
    @SerializedName("daily")
    DAILY,
    @SerializedName("weekly")
    WEEKLY,
    @SerializedName("monthly")
    MONTHLY,
    @SerializedName("yearly")
    YEARLY,
    @SerializedName("all_time")
    ALL_TIME;
}
