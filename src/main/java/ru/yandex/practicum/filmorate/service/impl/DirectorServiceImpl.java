package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorService;
import ru.yandex.practicum.filmorate.storage.dal.DirectorDal;

import java.util.Collection;
import java.util.List;

@Service
public class DirectorServiceImpl implements DirectorService {

    private final DirectorDal directorStorage;

    @Autowired
    public DirectorServiceImpl(DirectorDal directorStorage) {
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
        checkDirector(directors);
        directorStorage.updateFilmsDirector(id, directors);
    }

    @Override
    public Director updateDirector(Director director) {
        checkDirector(List.of(director));
        return directorStorage.updateDirector(director);
    }

    @Override
    public void deleteDirector(Long id) {
        directorStorage.deleteDirector(id);
    }

    private void checkDirector(Collection<Director> directors) {
        for (Director director : directors) {
            try {
                directorStorage.getDirectorById(director.getId());
            } catch (IllegalArgumentException e) {
                throw new NotFoundException("Жанр с ID: " + director.getId() + " не найден!");
            }
        }
    }
}
