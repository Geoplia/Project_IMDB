package com.uop.imdb.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.uop.imdb.model.enums.Gender;

// Represents a film director with their known works
public class Director extends Person {
    private final List<String> knownWorks = new ArrayList<>(); //list of works

    public Director(String firstName, String lastName, LocalDate birthDate, Gender gender, List<String> works) {
        super(firstName, lastName, birthDate, gender);
        if (works != null) this.knownWorks.addAll(works);
    }

    // Returns list
    public List<String> getKnownWorks() { return Collections.unmodifiableList(knownWorks); }

    //Adds a work
    public void addKnownWork(String title) {
        if (title == null || title.trim().isEmpty()) return;
        if (!knownWorks.contains(title)) knownWorks.add(title.trim());
    }

    @Override
    public String toString() {
        return String.format("Director %s (id=%d)", getFullName(), getId());
    }
}