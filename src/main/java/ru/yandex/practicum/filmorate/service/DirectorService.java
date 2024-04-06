package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Director;
import java.util.Collection;

public interface DirectorService {

    Collection<Director> getDirectors();

    Director getDirectorById(Long id);

    Director createDirector(Director director);

    Director updateDirector(Director director);

    void addFilmDirectors(Long id, Collection<Director> directors);

    void deleteDirector(Long id);

    Collection<Director> getFilmsDirector(Long id);
}
