package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.dal.GenreDal;

import java.util.Collection;

@Service
@Transactional
public class GenreServiceImpl implements GenreService {
    private final GenreDal genreDao;

    @Autowired
    public GenreServiceImpl(GenreDal genreDao) {
        this.genreDao = genreDao;
    }

    @Override
    public Collection<Genre> getGenres() {
        return genreDao.getGenres();
    }

    @Override
    public Genre getGenreById(Long id) throws NotFoundException {
        return genreDao.getGenreById(id);
    }


    public Collection<Genre> getFilmsGenre(Long id) {
        return genreDao.getFilmGenre(id);
    }

    @Override
    public void updateFilmsGenre(Long id, Collection<Genre> genres) {
        checkGenre(genres);
        genreDao.updateFilmsGenre(id, genres);
    }

    private void checkGenre(Collection<Genre> genres) {
        for (Genre genre : genres) {
            try {
                genreDao.getGenreById(genre.getId());
            } catch (IllegalArgumentException e) {
                throw new NotFoundException("Жанр с ID: " + genre.getId() + " не найден!");
            }
        }
    }
}
