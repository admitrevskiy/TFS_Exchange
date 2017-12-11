package com.example.tfs_exchange.model;

/**
 * Created by pusya on 10.11.17.
 * Модель истории обмена.
 * Нужен rate или для истории курса запилить отдельную БД?
 */

public class Exchange {
    private String currencyFrom;
    private String currencyTo;
    private double amountFrom;
    private double amountTo;
    private double rate;
    private String date;
    private String time;
    private long millis;

    public Exchange(String currencyFrom, String currencyTo, double amountFrom, double amountTo, double rate, String date, String time, long millis) {
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
        this.amountFrom = amountFrom;
        this.amountTo = amountTo;
        this.rate = rate;
        this.date = date;
        this.time = time;
        this.millis = millis;
    }

    public Exchange(String currencyFrom, String currencyTo, double amountFrom, double amountTo) {
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
        this.amountFrom = amountFrom;
        this.amountTo = amountTo;
    }

    public Exchange(String currencyFrom, String currencyTo, double amountFrom, double amountTo, String date, String time, long millis) {
        this.currencyFrom = currencyFrom;
        this.currencyTo = currencyTo;
        this.amountFrom = amountFrom;
        this.amountTo = amountTo;
        this.date = date;
        this.time = time;
        this.millis = millis;
    }

    @Override
    public String toString() {
        return currencyFrom + "  " + currencyTo + " " + amountFrom + " " + amountTo + " " + date + " " + time + " " + millis;
    }
    public double getRate() {
        return rate;
    }

    public long getMillis() {
        return millis;
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

    public String getTime() { return time; }

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

    public void setTime(String time) {
        this.time = time;
    }

    public void setMillis(long millis) {
        this.millis = millis;
    }
}
