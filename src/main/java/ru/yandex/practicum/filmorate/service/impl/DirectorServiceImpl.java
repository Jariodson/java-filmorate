package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.dal.DirectorDal;

import java.util.Collection;

@Service
public class DirectorServiceImpl implements DirectorService {

    private final DirectorDal directorStorage;

    @Autowired
    public DirectorServiceImpl(DirectorDal directorStorage) {
        this.directorStorage = directorStorage;
    }

    @Override
    public Collection<Director> getAllDirectors() {
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
    public void addNewDirector(Director director) {
        directorStorage.addNewDirector(director);
    }

    @Override
    public void addFilmsDirector(Long id, Collection<Director> directors) {
        directorStorage.addFilmsDirector(id, directors);
    }

    @Override
    public void updateDirector(Director director) {
        directorStorage.updateDirector(director);
        director = getDirectorById(director.getId());
    }

    @Override
    public void deleteDirector(Long id) {
        directorStorage.deleteDirector(id);
    }
}
