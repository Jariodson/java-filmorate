package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.ArrayList;
import java.util.Collection;

public interface DirectorService {

    Collection<Director> getAllDirectors();

    Director getDirectorById(Long id);

    void addNewDirector(Director director);

    void updateDirector(Director director);

    void addFilmsDirector(Long id, Collection<Director> directors);
    void deleteDirector(Long id);

    Collection<Director> getFilmsDirector(Long id);
}
