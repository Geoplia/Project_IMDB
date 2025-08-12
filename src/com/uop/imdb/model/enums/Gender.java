package com.uop.imdb.model.enums;

//ender options
public enum Gender {
    M, F;

    // Converts string input to Gender enum
    public static Gender fromString(String s) {
        if (s == null) return null;
        s = s.trim().toUpperCase();
        if (s.startsWith("M")) return M;
        if (s.startsWith("F")) return F;
        return null;
    }
}