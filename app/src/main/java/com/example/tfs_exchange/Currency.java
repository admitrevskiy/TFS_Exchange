package com.example.tfs_exchange;

/**
 * Created by pusya on 16.10.17.
 * Модель валюты для БД
 */

public class Currency {
    private String name;
    private long lastUse;
    private boolean favorite;
    private boolean longClicked;

    public Currency(String name, long lastUse, boolean favorite) {
        this.name = name;
        this.lastUse = lastUse;
        this.favorite = favorite;
        this.longClicked = false;
    }

    public Currency() {

    }

    public String getName() {
        return name;
    }

    public long getLastUse() {
        return lastUse;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastUse(long lastUse) {
        this.lastUse = lastUse;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public boolean isLongClicked() {
        return longClicked;
    }

    public void setLongClicked(boolean longClicked) {
        this.longClicked = longClicked;
    }
}
