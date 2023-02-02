package com.dacaspex.provider.reddit;

import com.google.gson.annotations.SerializedName;

public enum Sort {
    @SerializedName("hot")
    HOT,
    @SerializedName("best")
    BEST,
    @SerializedName("new")
    NEW,
    @SerializedName("rising")
    RISING,
    @SerializedName("controversial")
    CONTROVERSIAL,
    @SerializedName("top")
    TOP
}
