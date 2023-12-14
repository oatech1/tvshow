package org.example;

import java.io.Serializable;
import java.util.Date;

public class TVShow implements Serializable {
    private String title;
    private int season;
    private double rating;
    Date releaseDate;

    public TVShow(String title, int season, double rating, Date releaseDate) {
        this.title = title;
        this.season = season;
        this.rating = rating;
        this.releaseDate = releaseDate;
    }

    public Date getReleaseDate() {
        return releaseDate;
    }

    // Getters and Setters (Abstraction)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "Title: " + title + ", Season: " + season + ", Rating: " + rating;
    }

}
