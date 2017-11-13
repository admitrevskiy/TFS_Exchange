package com.example.tfs_exchange.api;

import com.google.gson.annotations.SerializedName;

/**
 * Created by pusya on 08.11.17.
 * В объекте используется отдельный класс RateObject вместо Map<String, JsonElement>, потому что из RateObject проще вытащить rate
 */

public class ApiResponse {
    @SerializedName("base")
    private String base;

    @SerializedName("date")
    private String date;

    private RateObject rates;

    public ApiResponse() {
    }

    public ApiResponse(String base, String date, RateObject rates) {
        this.base = base;
        this.date = date;
        this.rates = rates;
    }


    public String getBase() {
        return base;
    }

    public String getDate() {
        return date;
    }

    public RateObject getRates() {
        return rates;
    }

    @Override
    public String toString() {
        return "base: " + getBase() + " date: " + getDate() + " rates:\n" + getRates().getSymbols() + " : " +getRates().getRate();
    }
}
