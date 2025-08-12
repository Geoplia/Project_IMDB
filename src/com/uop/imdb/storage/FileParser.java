package com.uop.imdb.storage;

import com.uop.imdb.model.enums.Gender;
import com.uop.imdb.model.enums.Genre;
import com.uop.imdb.model.enums.Race;
import com.uop.imdb.exceptions.InvalidDataException;
import com.uop.imdb.model.*;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;


// Handles loading data from text files into DataStore
public class FileParser {
    private final DataStore store;

    public FileParser(DataStore store) { this.store = store; }


    // Main parsing entry point
    public void parseAll(String dataDir) throws IOException {
        parseUsers(new File(dataDir, "Users.txt"));
        parseActors(new File(dataDir, "Actors.txt"));
        parseDirectors(new File(dataDir, "Directors.txt"));
        parseMovies(new File(dataDir, "Movies.txt"));
        parseSeries(new File(dataDir, "Series.txt"));
    }

    public void parseUsers(File f) throws IOException {
        if (!f.exists()) return;
        List<String> lines = Files.readAllLines(f.toPath());
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            String[] parts = line.split(",");
            if (parts.length < 4) throw new IllegalArgumentException("Users.txt invalid line: " + line);
            String first = parts[0].trim();
            String last = parts[1].trim();
            String username = parts[2].trim();
            String email = parts[3].trim();
            store.addUser(new User(first, last, username, email));
        }
    }

    public void parseActors(File f) throws IOException {
        if (!f.exists()) return;
        List<String> lines = Files.readAllLines(f.toPath());
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;

            try {
                String[] parts = Arrays.stream(line.split(","))
                        .map(String::trim)
                        .toArray(String[]::new);

                if (parts.length < 2) {
                    System.err.println("Skipping actor: not enough data -> " + line);
                    continue;
                }

                String first = parts[0];
                String last = parts[1];

                // Birth date (optional)
                LocalDate birth = null;
                if (parts.length >= 3 && !parts[2].isEmpty()) {
                    try {
                        birth = LocalDate.parse(parts[2]);
                    } catch (Exception e) {
                        System.err.println("Invalid birth date for actor: " + line);
                    }
                }

                // Gender (optional)
                Gender g = null;
                if (parts.length >= 4 && !parts[3].isEmpty()) {
                    g = Gender.fromString(parts[3]);
                    if (g == null) {
                        System.err.println("Unknown gender for actor: " + line);
                    }
                }

                // Race (optional)
                Race r = null;
                if (parts.length >= 5 && !parts[4].isEmpty()) {
                    r = Race.fromString(parts[4]);
                    if (r == null) {
                        System.err.println("Unknown race for actor: " + line);
                    }
                }

                Actor a = new Actor(first, last, birth, g, r);
                store.addActor(a);

            } catch (Exception e) {
                System.err.println("Error parsing actor: " + line + " -> " + e.getMessage());
            }
        }
    }

    public void parseDirectors(File f) throws IOException {
        if (!f.exists()) return;
        List<String> lines = Files.readAllLines(f.toPath());
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            try {
                String[] parts = Arrays.stream(line.split(",", 5)).map(String::trim).toArray(String[]::new);
                if (parts.length < 5) throw new InvalidDataException("Directors.txt malformed: " + line);
                String first = parts[0];
                String last = parts[1];
                LocalDate birth = null;
                try { birth = LocalDate.parse(parts[2]); } catch (Exception ex) {}
                Gender g = Gender.fromString(parts[3]);
                List<String> works = Arrays.stream(parts[4].split("\\|"))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
                Director d = new Director(first, last, birth, g, works);
                store.addDirector(d);
            } catch (InvalidDataException e) {
                System.err.println("Skipping director line: " + e.getMessage());
            }
        }
    }

    public void parseMovies(File f) throws IOException {
        if (!f.exists()) return;
        List<String> lines = Files.readAllLines(f.toPath());
        for (String line : lines) {
            if (line.trim().isEmpty()) continue;
            try {
                // expected: title,year,genre,duration,director,imdbRating,mainActor,1:9|2:10...
                String[] parts = Arrays.stream(line.split(",", 8)).map(String::trim).toArray(String[]::new);
                if (parts.length < 7) throw new InvalidDataException("Movies.txt malformed: " + line);

                String title = parts[0];
                int year = Integer.parseInt(parts[1]);
                Genre genre = Genre.fromString(parts[2]);
                int duration = Integer.parseInt(parts[3]);
                String directorName = parts[4];
                double imdb = Double.parseDouble(parts[5]);
                String actorName = parts[6];

                Director director = store.findDirectorByName(directorName).orElse(null);
                Actor actor = store.findActorByName(actorName).orElse(null);

                Movie m = new Movie(title, year, genre, duration,
                        director == null ? createPlaceholderDirector(directorName) : director,
                        imdb, actor);

                if (parts.length >= 8) {
                    String ratings = parts[7];
                    if (!ratings.isEmpty()) {
                        String[] tokens = ratings.split("\\|");
                        for (String tok : tokens) {
                            String[] kv = tok.split(":");
                            if (kv.length == 2) {
                                int uid = Integer.parseInt(kv[0]);
                                int r = Integer.parseInt(kv[1]);
                                m.addUserRating(uid, r);
                            }
                        }
                    }
                }
                store.addMovie(m);
            } catch (InvalidDataException e) {
                System.err.println("Skipping movie line: " + e.getMessage());
            }
        }
    }

    private Director createPlaceholderDirector(String name) {
        // crude split
        String[] p = name.split(" ");
        String first = p.length > 0 ? p[0] : name;
        String last = p.length > 1 ? String.join(" ", Arrays.copyOfRange(p, 1, p.length)) : "";
        return new Director(first, last, null, null, null);
    }

    public void parseSeries(File f) throws IOException {
        if (!f.exists()) return;
        List<String> lines = Files.readAllLines(f.toPath());
        Series currentSeries = null;
        Season currentSeason = null;
        for (String raw : lines) {
            String line = raw.trim();
            if (line.isEmpty()) continue;
            if (line.startsWith("SERIES:")) {
                // SERIES:Breaking Bad,Crime,1:10|2:9|...
                String payload = line.substring("SERIES:".length()).trim();
                String[] header = payload.split(",", 3);
                String title = header[0].trim();
                Genre g = Genre.fromString(header.length > 1 ? header[1].trim() : null);
                Series s = new Series(title, g);
                // parse user ratings if present
                if (header.length > 2) {
                    String ratings = header[2];
                    if (!ratings.isEmpty()) {
                        String[] tokens = ratings.split("\\|");
                        int userIndex = 1;
                        for (String tok : tokens) {
                            if (tok.isEmpty()) continue;
                            try {
                                int rating = Integer.parseInt(tok.trim());
                                s.addUserRating(userIndex++, rating);
                            } catch (NumberFormatException e) { /* skip */ }
                        }
                    }
                }
                store.addSeries(s);
                currentSeries = s;
                currentSeason = null;
            } else if (line.startsWith("SEASON:")) {
                // SEASON:1, 2008:
                String payload = line.substring("SEASON:".length()).trim();
                // try to extract year
                String[] parts = payload.split(",");
                int year = 0;
                if (parts.length >= 2) {
                    try { year = Integer.parseInt(parts[1].trim().replace(":", "")); } catch (Exception ex) {}
                }
                currentSeason = new Season(year);
                if (currentSeries != null) currentSeries.addSeason(currentSeason);
            } else {
                // episode line: duration,director,9.1,actor
                String[] p = Arrays.stream(line.split(",")).map(String::trim).toArray(String[]::new);
                if (p.length >= 4) {
                    int dur = Integer.parseInt(p[0]);
                    String directorName = p[1];
                    double imdb = Double.parseDouble(p[2]);
                    String actorName = p[3];
                    Director director = store.findDirectorByName(directorName).orElseGet(() -> createPlaceholderDirector(directorName));
                    Actor actor = store.findActorByName(actorName).orElse(null);
                    Episode ep = new Episode(dur, director, imdb, actor);
                    if (currentSeason != null) currentSeason.addEpisode(ep);
                }
            }
        }
    }
}
