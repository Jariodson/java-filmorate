package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final Map<String, Film> films = new HashMap<>();

    @GetMapping
    public Collection<Film> getFilms() {
        return films.values();
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        checkFilmCriteria(film);
        if (films.containsKey(film.getName())) {
            throw new ValidationException("Фильм с названием " + film.getName() + "уже добавлен");
        }
        films.put(film.getName(), film);
        log.debug("Добавление фильма: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        checkFilmCriteria(film);
        films.put(film.getName(), film);
        log.debug("Перезапись фильма: {}", film);
        return film;
    }

    private void checkFilmCriteria(Film film) {
        LocalDate filmBirthday = LocalDate.of(1895, 12, 28);
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Введено пустое название фильма: {}", film.getName());
            throw new ValidationException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Введенно слищком большое описание: {} из 200", film.getDescription().length());
            throw new ValidationException("Длина описания превышает лимит! Лимит 200!");
        }
        if (film.getReleaseDate().isBefore(filmBirthday)) {
            log.warn("Введена слишком ранняя дата релиза: {}", film.getReleaseDate());
            throw new ValidationException("Слшиком ранняя дата релиза!");
        }
        if (film.getDuration() < 0) {
            log.warn("Введена отрицательная продолжительность фильма: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма должна быть положительной!");
        }
    }
}
