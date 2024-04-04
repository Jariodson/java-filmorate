package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorService {

    Collection<Director> getAllDirectors();

    Director getDirectorById(Long id);

    void addNewDirector(Director director);

    void updateDirector(Director director);
}
