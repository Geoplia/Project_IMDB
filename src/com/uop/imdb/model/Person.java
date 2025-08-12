package com.uop.imdb.model;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;
import com.uop.imdb.model.enums.Gender;

public abstract class Person {
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);
    protected final int id;
    protected String firstName;
    protected String lastName;
    protected LocalDate birthDate;
    protected Gender gender;

    protected Person(String firstName, String lastName, LocalDate birthDate, Gender gender) {
        this.id = NEXT_ID.getAndIncrement(); // Auto-incremented unique I
        setFirstName(firstName);
        setLastName(lastName);
        this.birthDate = birthDate; // null allowed in parsing if unknown
        this.gender = gender;
    }

    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) {
        if (firstName == null || firstName.trim().isEmpty()) throw new IllegalArgumentException("First name required");
        this.firstName = firstName.trim();
    }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) {
        if (lastName == null || lastName.trim().isEmpty()) throw new IllegalArgumentException("Last name required");
        this.lastName = lastName.trim();
    }
    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate d) { this.birthDate = d; }
    public Gender getGender() { return gender; }
    public void setGender(Gender g) { this.gender = g; }

    public String getFullName() { return firstName + " " + lastName; }

    @Override
    public String toString() {
        return String.format("[%d] %s %s", id, firstName, lastName);
    }
}