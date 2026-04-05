package com.moviereservationsystem.enums;

/**
 * Enumeration for Movie Categories.
 * Linked to Movie entity via @ElementCollection in the movie_genres table.
 */
public enum Genre {
    ACTION,
    DRAMA,
    COMEDY,
    THRILLER,
    HORROR,
    SCI_FI,
    ROMANCE,
    ANIMATION,
    BIOGRAPHY
}