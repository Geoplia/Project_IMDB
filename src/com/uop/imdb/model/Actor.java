package com.uop.imdb.model;

import java.time.LocalDate;

import com.uop.imdb.model.enums.Gender;
import com.uop.imdb.model.enums.Race;
import com.uop.imdb.model.enums.Genre;

// Represents an actor, extending Person with race information
public class Actor extends Person {
    private Race race;

    public Actor(String firstName, String lastName, LocalDate birthDate, Gender gender, Race race) {
        super(firstName, lastName, birthDate, gender);  // Initialize Person fields
        this.race = race;
    }

    // Basic getter/setter for race
    public Race getRace() { return race; }
    public void setRace(Race r) { this.race = r; }

    @Override
    public String toString() {
        return String.format("Actor %s (id=%d)", getFullName(), getId());
    }
}
