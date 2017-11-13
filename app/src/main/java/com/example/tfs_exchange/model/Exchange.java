package com.example.tfs_exchange.model;

import java.util.Date;

/**
 * Created by pusya on 10.11.17.
 */

public class Exchange {
    private String currencyFrom;
    private String currencyTo;
    private double amountFrom;
    private double amountTo;
    private double rate;
    private String date;

    public Exchange(String currencyFrom, String currencyTo, double amountFrom, double amountTo, double rate, String date) {
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
        this.amountFrom = amountFrom;
        this.amountTo = amountTo;
        this.rate = rate;
        this.date = date;
    }


    public String getCurrencyFrom() {
        return currencyFrom;
    }

    public String getCurrencyTo() {
        return currencyTo;
    }

    public double getAmountFrom() {
        return amountFrom;
    }

    public double getAmountTo() {
        return amountTo;
    }

    public String getDate() {
        return date;
    }

    public void setCurrencyFrom(String currencyFrom) {
        this.currencyFrom = currencyFrom;
    }

    public void setCurrencyTo(String currencyTo) {
        this.currencyTo = currencyTo;
    }

    public void setAmountFrom(double amountFrom) {
        this.amountFrom = amountFrom;
    }

    public void setAmountTo(double amountTo) {
        this.amountTo = amountTo;
    }

    public void setDate(String date) {
        this.date = date;
    }
}