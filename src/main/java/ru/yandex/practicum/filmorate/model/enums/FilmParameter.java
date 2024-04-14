package ru.yandex.practicum.filmorate.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum FilmParameter {
    DIRECTOR,
    TITLE,
    DIR_AND_TITLE,
    TITLE_AND_DIR
}