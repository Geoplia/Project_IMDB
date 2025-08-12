package com.uop.imdb.storage;

import com.uop.imdb.model.*;

import java.util.*;
import java.util.stream.Collectors;

// Central database
public class DataStore {
    // Collections for each entity type
    private final List<User> users = new ArrayList<>();
    private final List<Actor> actors = new ArrayList<>();
    private final List<Director> directors = new ArrayList<>();
    private final List<Movie> movies = new ArrayList<>();
    private final List<Series> seriesList = new ArrayList<>();


    // add methods
    public void addUser(User u) { users.add(u); }
    public void addActor(Actor a) { actors.add(a); }
    public void addDirector(Director d) { directors.add(d); }
    public void addMovie(Movie m) {
        movies.add(m);
        // Auto-add to director's known works if highly rated
        if (m.getImdbRating() > 7.5) m.getDirector().addKnownWork(m.getTitle());
    }
    public void addSeries(Series s) {
        seriesList.add(s);
    }

    public Optional<User> findUserByUsername(String username) {
        return users.stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst();
    }

    public Optional<Movie> findMovieById(int id) {
        return movies.stream().filter(m -> m.getId() == id).findFirst();
    }

    // Search methods using Optional for null safety
    public Optional<Actor> findActorByName(String token) {
        if (token == null) return Optional.empty();
        String t = token.trim().toLowerCase();
        return actors.stream().filter(a -> {
            String full = a.getFullName().toLowerCase();
            String first = a.getFirstName().toLowerCase();
            String last = a.getLastName().toLowerCase();

            // Match full name, first name, or last name
            if (full.equals(t) || first.equals(t) || last.equals(t)) return true;

            // Also allow partial matches like "DiCaprio" matching "Leonardo DiCaprio"
            return full.contains(t) || first.contains(t) || last.contains(t);
        }).findFirst();
    }
    public Optional<Director> findDirectorByName(String token) {
        String t = token.trim().toLowerCase();
        return directors.stream().filter(d ->
            d.getFullName().toLowerCase().equals(t) ||
            d.getFirstName().toLowerCase().equals(t) ||
            d.getLastName().toLowerCase().equals(t)
        ).findFirst();
    }
    public Optional<Movie> findMovieByTitle(String title) {
        String t = title.trim().toLowerCase();
        return movies.stream().filter(m -> m.getTitle().toLowerCase().equals(t)).findFirst();
    }
    public Optional<Series> findSeriesByTitle(String title) {
        String t = title.trim().toLowerCase();
        return seriesList.stream().filter(s -> s.getTitle().toLowerCase().equals(t)).findFirst();
    }

    public List<Movie> getAllMoviesSortedByUserRatingDesc() {
        return movies.stream().sorted(Comparator.comparingDouble(Movie::getAverageUserRating).reversed()).collect(Collectors.toList());
    }
    public List<Series> getAllSeries() { return Collections.unmodifiableList(seriesList); }

    // Simple search by filters
    public List<Movie> searchMovies(String titleOrActorOrDirector, Double minImdb, Double minUserRating) {
        String tok = titleOrActorOrDirector == null ? "" : titleOrActorOrDirector.trim().toLowerCase();
        return movies.stream().filter(m -> {
            boolean ok = true;
            if (!tok.isEmpty()) {
                ok = m.getTitle().toLowerCase().contains(tok)
                    || (m.getMainActor() != null && m.getMainActor().getFullName().toLowerCase().contains(tok))
                    || (m.getDirector() != null && m.getDirector().getFullName().toLowerCase().contains(tok));
            }
            if (ok && minImdb != null) ok = m.getImdbRating() >= minImdb;
            if (ok && minUserRating != null) ok = m.getAverageUserRating() >= minUserRating;
            return ok;
        }).collect(Collectors.toList());
    }

    // Accessors for UI
    public List<User> getUsers() { return Collections.unmodifiableList(users); }
    public List<Actor> getActors() { return Collections.unmodifiableList(actors); }
    public List<Director> getDirectors() { return Collections.unmodifiableList(directors); }

    public List<Movie> getMovies() {
        return Collections.unmodifiableList(movies);
    }

}