package ru.yandex.practicum.filmorate.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum SortParam {
    year,
    likes
}
