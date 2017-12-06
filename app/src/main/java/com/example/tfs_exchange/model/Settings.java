package com.example.tfs_exchange.model;

import java.util.Set;

/**
 * Created by pusya on 01.12.17.
 */

public class Settings {
    private int period_id;
    private Set<String> currencies;
    private long dateFrom;
    private long dateTo;

    @Override
    public String toString() {
        if (currencies != null && (dateFrom != 0 && dateTo != 0))
            return "Settings: " + period_id + " " + currencies.toString() + " " + dateFrom + " " + dateTo;
        else {
            return "Settings: " + period_id + " " + dateFrom + " " + dateTo;
        }
    }

    public Settings(){

    }

    public Settings(int period_id) {
        this.period_id = period_id;
    }

    public Settings(int period_id, long dateFrom, long dateTo) {
        this.period_id = period_id;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public Settings(int period_id, Set<String> currencies) {
        this.period_id = period_id;
        this.currencies = currencies;
    }

    public Settings(int period_id, Set<String> currencies, long dateFrom, long dateTo) {
        this.period_id = period_id;
        this.currencies = currencies;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    public void setPeriod_id(int period_id) {
        this.period_id = period_id;
    }

    public void setCurrencies(Set<String> currencies) {
        this.currencies = currencies;
    }

    public void setDateFrom(long dateFrom) {
        this.dateFrom = dateFrom;
    }

    public void setDateTo(long dateTo) {
        this.dateTo = dateTo;
    }

    public int getPeriod_id() {
        return period_id;
    }

    public Set<String> getCurrencies() {
        return currencies;
    }

    public long getDateFrom() {
        return dateFrom;
    }

    public long getDateTo() {
        return dateTo;
    }
}
