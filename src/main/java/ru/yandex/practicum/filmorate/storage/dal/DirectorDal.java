package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.Collection;

public interface DirectorDal {
    public Collection<Director> getAllDirectors();

    public Director getDirectorById(Long id);

    public void addNewDirector(Director newDirector);

    public void updateDirector(Director director);

}
