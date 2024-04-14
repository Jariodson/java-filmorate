package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.DirectorStorage;

import java.util.Collection;

@Service
public class DirectorServiceImpl implements DirectorService {

    private final DirectorStorage directorStorage;

    @Autowired
    public DirectorServiceImpl(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    @Override
    public Collection<Director> getDirectors() {
        return directorStorage.getAllDirectors();
    }

    @Override
    public Director getDirectorById(Long id) {
        return directorStorage.getDirectorById(id);
    }

    @Override
    public Collection<Director> getFilmsDirector(Long id) {
        return directorStorage.getFilmsDirector(id);
    }

    @Override
    public Director createDirector(Director director) {
        return directorStorage.addNewDirector(director);
    }

    @Override
    public void updateFilmDirectors(Long id, Collection<Director> directors) {
        for (Director director : directors) {
            validateDirectorId(director.getId());
        }
        directorStorage.updateFilmsDirector(id, directors);
    }

    @Override
    public Director updateDirector(Director director) {
        validateDirectorId(director.getId());
        return directorStorage.updateDirector(director);
    }

    @Override
    public void deleteDirector(Long id) {
        directorStorage.deleteDirector(id);
    }

    @Override
    public void validateDirectorId(Long directorId) {
        try {
            directorStorage.getDirectorById(directorId);
        } catch (IllegalArgumentException e) {
            throw new NotFoundException("Жанр с ID: " + directorId + " не найден!");
        }

    }
}
