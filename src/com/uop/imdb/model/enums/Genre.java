package com.uop.imdb.model.enums;

// All possible movie/TV show genres in the system
public enum Genre {
    ACTION, ADULT, ADVENTURE, ANIMATION, BIOGRAPHY, COMEDY, CRIME, DOCUMENTARY,
    DRAMA, FAMILY, FANTASY, FILM_NOIR, GAME_SHOW, HISTORY, HORROR, MUSICAL,
    MUSIC, MYSTERY, ROMANCE, SCI_FI, SPORT, THRILLER, WAR, WESTERN;

    public static Genre fromString(String s) {
        if (s == null) return null;
        s = s.trim().toUpperCase().replace('-', '_').replace(" ", "_");

        // Special cases for genres with multiple common spellings
        switch (s) {
            case "SCI-FI": case "SCIFI": case "SCI_FI": return SCI_FI;
            case "FILM NOIR": case "FILM_NOIR": return FILM_NOIR;
            case "GAME SHOW": case "GAME_SHOW": return GAME_SHOW;
            default:
                try { return Genre.valueOf(s); }
                catch (Exception e) { return null; }
        }
    }

    // Formats enum for display (e.g., "FILM_NOIR" → "FILM NOIR")
    @Override public String toString() {
        return name().replace('_', ' ');
    }
}
