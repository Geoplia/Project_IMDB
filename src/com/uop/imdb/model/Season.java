package com.uop.imdb.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

// Represents a TV season containing episodes
public class Season {
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);
    private final int id;
    private final int year;
    private final List<Episode> episodes = new ArrayList<>();

    public Season(int year) {
        this.id = NEXT_ID.getAndIncrement();
        this.year = year;
    }

    public int getId() { return id; }
    public int getYear() { return year; }

    // Returns immutable list to prevent external modification

    public List<Episode> getEpisodes()
    {
        return Collections.unmodifiableList(episodes);
    }

    // Null-safe episode addition
    public void addEpisode(Episode e) {
        if (e != null) episodes.add(e);
    }

    @Override
    public String toString() {
        return String.format("Season %d (%d episodes)", year, episodes.size());
    }
}