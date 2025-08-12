package com.uop.imdb.model;

import java.util.concurrent.atomic.AtomicInteger;

// Represents a single episode in a TV series
public class Episode {
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1); // Auto-increment ID
    private final int id;
    private int durationMinutes;
    private Director director;
    private double imdbRating;
    private Actor mainActor;

    public Episode(int durationMinutes, Director director, double imdbRating, Actor mainActor) {
        this.id = NEXT_ID.getAndIncrement();// Assign unique ID
        this.durationMinutes = durationMinutes;
        this.director = director;
        this.imdbRating = imdbRating;
        this.mainActor = mainActor;
    }

    // Basic getters
    public int getId() { return id; }
    public int getDurationMinutes() { return durationMinutes; }
    public Director getDirector() { return director; }
    public double getImdbRating() { return imdbRating; }
    public Actor getMainActor() { return mainActor; }

    @Override
    public String toString() {
        return String.format("Ep[%d] %dmin, imdb=%.1f, dir=%s, actor=%s", id, durationMinutes, imdbRating,
                director == null ? "?" : director.getFullName(),
                mainActor == null ? "?" : mainActor.getFullName());
    }
}