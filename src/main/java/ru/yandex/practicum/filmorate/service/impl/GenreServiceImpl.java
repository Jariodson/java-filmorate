package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
        checkGenre(id);
        return genreDao.getGenreById(id);
    }

    @Override
    public String getGenreNameById(Long id) {
        checkGenre(id);
        return genreDao.getGenreNameById(id);
    }

    private void checkGenre(Long id) {
        try {
            genreDao.getGenreById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Жанр с ID: " + id + " не найден!");
        }
    }

    public Collection<Genre> getFilmsGenre(Long id) {
        return genreDao.getFilmGenre(id);

    }

    @Override
    public void addFilmsGenre(Long userId,Collection<Genre> genres) {
        genreDao.addFilmsGenre(userId, genres);
    }
}
