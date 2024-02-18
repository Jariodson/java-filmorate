package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
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
            log.warn("Фильм с названием {} уже добавлен", film.getName());
            throw new ValidationException("Фильм с названием " + film.getName() + " уже добавлен");
        }
        films.put(film.getId(), film);
    }

    @Override
    public void updateFilm(Film film) {
        checkFilmCriteria(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            return;
        }
        log.warn("Фильм не содержится в списке!");
        throw new IllegalArgumentException("Такого фильма не существует!");
    }

    @Override
    public Film getFilmById(Long id) {
        if (films.containsKey(id)) {
            return films.get(id);
        }
        log.warn("Ошибка! Фильм с Id: {} не найден!", id);
        throw new IllegalArgumentException("Фильм с Id " + id + " не найден");
    }

    @Override
    public void deleteFilm(Film film) {
        if (!films.containsValue(film)) {
            log.warn("Ошибка! Фильма {} не существует!", film);
            throw new IllegalArgumentException("Такого фильма не сушествует!");
        }
        films.remove(film.getId());
    }

    private void checkFilmCriteria(Film film) {
        LocalDate filmBirthday = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(filmBirthday)) {
            log.warn("Введена слишком ранняя дата релиза: {}", film.getReleaseDate());
            throw new ValidationException("Слшиком ранняя дата релиза!");
        }
        if (film.getId() == 0) {
            film.setId(++genId);
            log.info("Фильму присвоен ID: {}", film.getId());
        }
    }
}
