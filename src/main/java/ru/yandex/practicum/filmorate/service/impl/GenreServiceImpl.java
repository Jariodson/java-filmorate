package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;
import ru.yandex.practicum.filmorate.storage.dal.GenreDal;

import java.util.Collection;

@Service
public class GenreServiceImpl implements GenreService {
    @Autowired
    private GenreDal genreDao;

    @Override
    public Collection<Genre> getGenres() {
        return genreDao.getGenres();
    }

    @Override
    public Genre getGenreById(Long id) {
        return genreDao.getGenreById(id);
    }
}
