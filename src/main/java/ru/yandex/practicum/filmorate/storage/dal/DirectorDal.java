package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorDal {
    Collection<Director> getAllDirectors();

    Director getDirectorById(Long id);

    void addNewDirector(Director newDirector);

    void updateDirector(Director director);

    void addFilmsDirector(Long id, Collection<Director> directors);


    Collection<Director> getFilmsDirector(Long filmId);

    void deleteDirector(Long id);
}
