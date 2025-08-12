package com.uop.imdb.model;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

// Represents an IMDB user account
public class User {
    private static final AtomicInteger NEXT_ID = new AtomicInteger(1);
    private static final Pattern EMAIL_RE = Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    private final int id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;

    public User(String firstName, String lastName, String username, String email) {
        this.id = NEXT_ID.getAndIncrement();
        setFirstName(firstName);
        setLastName(lastName);
        setUsername(username);
        setEmail(email);
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

    public String getUsername() { return username; }
    public void setUsername(String username) {
        if (username == null || username.trim().isEmpty()) throw new IllegalArgumentException("Username required");
        this.username = username.trim();
    }

    // email validation - regex
    public String getEmail() { return email; }
    public void setEmail(String email) {
        if (email == null || !EMAIL_RE.matcher(email).matches()) throw new IllegalArgumentException("Invalid email");
        this.email = email;
    }

    @Override
    public String toString() {
        return String.format("[%d] %s %s (%s)", id, firstName, lastName, username);
    }
}
