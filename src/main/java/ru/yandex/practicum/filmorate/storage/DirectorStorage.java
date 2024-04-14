package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorStorage {
    Collection<Director> getAllDirectors();

    Director getDirectorById(Long id);

    Director addNewDirector(Director newDirector);

    Director updateDirector(Director director);

    void updateFilmsDirector(Long id, Collection<Director> directors);


    Collection<Director> getFilmsDirector(Long filmId);

    void deleteDirector(Long id);
}
