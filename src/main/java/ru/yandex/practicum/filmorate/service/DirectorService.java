package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorService {

    Collection<Director> getDirectors();

    Director getDirectorById(Long id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void deleteDirector(Long id);

    Collection<Director> getFilmsDirector(Long id);

    void updateFilmDirectors(Long id, Collection<Director> directors);
}
