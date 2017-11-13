package com.example.tfs_exchange.api;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by pusya on 07.11.17.
 */

public class RateObject {


    private String symbols;

    private double rate;

    public RateObject() {
    }

    public RateObject(String symbols, double rate) {
        this.symbols = symbols;
        this.rate = rate;
    }


    public String getSymbols() {
        return symbols;
    }

    public double getRate() {
        return rate;
    }

}
