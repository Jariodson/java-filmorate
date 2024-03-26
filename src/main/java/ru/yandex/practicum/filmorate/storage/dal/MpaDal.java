package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.Collection;

public interface MpaDal {
    Collection<Mpa> getMpa();
    Mpa getMpaById(Long id);
}
