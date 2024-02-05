package ru.yandex.practicum.filmorate.model;

import lombok.Value;

import java.time.LocalDate;

/**
 * Film.
 */
@Value
public class Film {
    int id;
    String name;
    String description;
    LocalDate releaseDate;
    int duration;
}
