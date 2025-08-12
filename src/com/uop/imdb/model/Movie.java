package com.uop.imdb.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import com.uop.imdb.model.enums.Genre;

// Represents a movie with all metadata and user ratings
public class Movie {
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);
    private final int id;
    private String title;
    private int year;
    private Genre genre;
    private int durationMinutes;
    private Director director;
    private double imdbRating; // 1-10
    private Actor mainActor;
    private final Map<Integer,Integer> userRatings = new HashMap<>(); // userId → rating (1-10)

    public Movie(String title, int year, Genre genre, int durationMinutes, Director director, double imdbRating, Actor mainActor) {
        this.id = NEXT_ID.getAndIncrement();
        setTitle(title);  // setters for validation
        setYear(year);
        setGenre(genre);
        setDurationMinutes(durationMinutes);
        setDirector(director);
        setImdbRating(imdbRating);
        setMainActor(mainActor);
    }


    //getters/setters for id/year/genre/director/rating/actor
    public int getId() { return id; }
    public String getTitle() { return title; }
    public void setTitle(String title) {
        if (title == null || title.trim().isEmpty()) throw new IllegalArgumentException("Title required");
        this.title = title.trim();
    }
    public int getYear() { return year; }
    public void setYear(int year) {
        if (year < 1870) throw new IllegalArgumentException("Year unrealistic");
        this.year = year;
    }
    public Genre getGenre() { return genre; }
    public void setGenre(Genre genre) { this.genre = genre; }
    public int getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(int durationMinutes) {
        if (durationMinutes <= 0) throw new IllegalArgumentException("Duration positive");
        this.durationMinutes = durationMinutes;
    }
    public Director getDirector() { return director; }
    public void setDirector(Director director) {
        if (director == null) throw new IllegalArgumentException("Director required");
        this.director = director;
    }
    public double getImdbRating() { return imdbRating; }
    public void setImdbRating(double imdbRating) {
        if (imdbRating < 0.0 || imdbRating > 10.0) throw new IllegalArgumentException("IMDb rating 0-10");
        this.imdbRating = imdbRating;
    }
    public Actor getMainActor() { return mainActor; }
    public void setMainActor(Actor mainActor) { this.mainActor = mainActor; }

    public Map<Integer,Integer> getUserRatings() { return Collections.unmodifiableMap(userRatings); }


    // Rating management
    public void addUserRating(int userId, int rating) {
        if (rating < 1 || rating > 10) throw new IllegalArgumentException("User rating 1-10");
        userRatings.put(userId, rating);
    }

    public double getAverageUserRating() {
        if (userRatings.isEmpty()) return 0.0;
        return userRatings.values().stream().mapToInt(Integer::intValue).average().orElse(0.0);
    }

    public String getDetails() {
        return String.format(
                "Title: %s%n" +
                        "Year: %d%n" +
                        "Genre: %s%n" +
                        "Duration: %d minutes%n" +
                        "Director: %s%n" +
                        "Main Actor: %s%n" +
                        "IMDb Rating: %.1f%n" +
                        "User Average: %.2f",
                getTitle(),
                getYear(),
                (getGenre() != null ? getGenre() : "N/A"),
                getDurationMinutes(),
                (getDirector() != null ? getDirector().getFullName() : "N/A"),
                (getMainActor() != null ? getMainActor().getFullName() : "N/A"),
                getImdbRating(),
                getAverageUserRating()
        );
    }

    @Override
    public String toString() {
        return String.format("%s (%d) - imdb=%.1f, user=%.2f", title, year, imdbRating, getAverageUserRating());
    }


}
