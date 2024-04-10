package ru.yandex.practicum.filmorate.storage.memory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.dal.FilmStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private static long genId = 0;
    private final Map<Long, Film> films = new HashMap<>();

    @Override
    public Collection<Film> getAllFilms() {
        return films.values();
    }

    @Override
    public void addNewFilm(Film film) {
        checkFilmCriteria(film);
        if (films.containsKey(film.getId())) {
            throw new ValidationException("Фильм с Id: {} " + film.getId() + " уже добавлен");
        }
        film.setId(++genId);
        films.put(film.getId(), film);
    }

    @Override
    public void updateFilm(Film film) {
        checkFilmCriteria(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return;
        }
        throw new IllegalArgumentException("Такого фильма не существует! Фильм: " + film);
    }

    @Override
    public Film getFilmById(Long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        }
        throw new IllegalArgumentException("Фильм с Id " + id + " не найден");
    }

    @Override
    public void deleteFilm(Long id) {
        if (!films.containsKey(id)) {
            throw new IllegalArgumentException("Такого фильма не сушествует! ID фильма:" + id);
        }
        films.remove(id);
    }

    @Override
    public Collection<Film> getFilmsByDirectorAndSort(Long directorId, String[] orderBy) {
        return null;
    }


    @Override
    public Collection<Film> getCommonFilms(Long userId, Long friendId) {
        return null;
    }

    @Override
    public Collection<Film> getMostPopularsFilms(Integer count, Long genreId, Integer year) {
        return null;
    }

    @Override
    public List<Film> searchFilmByParameter(String query, String filmSearchParameter) {
        return null;
    }

    private void checkFilmCriteria(Film film) {
        LocalDate filmBirthday = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(filmBirthday)) {
            throw new ValidationException("Слишком ранняя дата релиза! " + film.getReleaseDate());
        }
    }

}