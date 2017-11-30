package com.example.tfs_exchange.model;

/**
 * Created by pusya on 16.10.17.
 * Модель валюты для БД
 */

public class Currency {
    private String name;
    private long lastUse;
    private boolean favorite;
    private boolean longClicked;
    private boolean isFilter;

    /** Конструкторы **/
    public Currency(String name, long lastUse, boolean favorite) {
        this.name = name;
        this.lastUse = lastUse;
        this.favorite = favorite;
        this.longClicked = false;
    }

    public Currency(String name, boolean isFilter) {
        this.name = name;
        this.isFilter = isFilter;
        this.longClicked = false;
    }

    public Currency(String name) {
        this.name = name;

    }

    public Currency() {

    }

    /** Геттеры и сеттеры **/
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

    public void setFilter(boolean filter) {
        isFilter = filter;
    }

    public boolean isFilter() {
        return isFilter;
    }



    /** переписываем методы equals и hashCode для корректной работы HashSet в AsyncCurrencyDBLoader при загрузке для экрана фильтров **/
    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (!Currency.class.isAssignableFrom(o.getClass())) {
            return false;
        }
        final Currency other = (Currency)o;
        if ((this.getName() == null) ? (other.getName() != null) : !this.getName().equals(other.getName())) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash +=  (int)((char)this.getName().charAt(1));
        return hash;
    }
}
