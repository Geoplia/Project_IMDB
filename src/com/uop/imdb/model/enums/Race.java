package com.uop.imdb.model.enums;

// Ethnic categories
public enum Race {
    CAUCASOID, NEGROID, MONGOLOID, ASIAN, PACIFIC_ISLAND_AND_AUSTRALIAN, AMERINDIANS_AND_ESKIMOS;

    public static Race fromString(String s) {
        if (s == null) return null;
        s = s.trim().toUpperCase();
        s = s.replace(" ", "_").replace("-", "_").replace("&", "AND");

        // Handle multi-word categories
        if (s.contains("PACIFIC")) return PACIFIC_ISLAND_AND_AUSTRALIAN;
        if (s.contains("AMERIND")) return AMERINDIANS_AND_ESKIMOS;

        try { return Race.valueOf(s); }
        catch (Exception e) { return null; }
    }
}