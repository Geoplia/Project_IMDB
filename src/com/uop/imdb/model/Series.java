package com.uop.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import com.uop.imdb.model.enums.Genre;


// Represents a TV series with seasons and ratings
public class Series {
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);
    private final int id;
    private String title;
    private Genre genre;
    private final List<Season> seasons = new ArrayList<>();
    private final Map<Integer,Integer> userRatings = new HashMap<>(); // userId -> rating

    public Series(String title, Genre genre) {
        this.id = NEXT_ID.getAndIncrement();
        setTitle(title);
        setGenre(genre);
    }

    public int getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String t) {
        if (t == null || t.trim().isEmpty()) throw new IllegalArgumentException("Title required");
        this.title = t.trim();
    }
    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }

    public List<Season> getSeasons() { return Collections.unmodifiableList(seasons); }
    public void addSeason(Season s) {
        if (s != null) seasons.add(s);
    }
    public Map<Integer,Integer> getUserRatings() { return Collections.unmodifiableMap(userRatings); }

    // Rating management (similar to Movie class)
    public void addUserRating(int userId, int rating) {
        if (rating < 1 || rating > 10) throw new IllegalArgumentException("User rating 1-10");
        userRatings.put(userId, rating);
    }

    //Calculates average rating (0.0 if no ratings)
    public double getAverageUserRating() {
        if (userRatings.isEmpty()) return 0.0;
        return userRatings.values().stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }


    @Override
    public String toString() {
        return String.format("Series %s (id=%d) user=%.2f", title, id, getAverageUserRating());
    }
}
