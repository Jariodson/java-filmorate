package ru.yandex.practicum.filmorate.model.enums;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@JsonFormat(shape = JsonFormat.Shape.STRING)
public enum SortParam {
    year,
    like
}
