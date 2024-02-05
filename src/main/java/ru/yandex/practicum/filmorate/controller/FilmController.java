package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/films")
@ResponseStatus(HttpStatus.NOT_FOUND)
@Slf4j
public class FilmController {
    private static int genId = 0;
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> getFilms() {
        log.debug("Вывод всех фильмов");
        return films.values();
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@RequestBody Film film) {
        checkFilmCriteria(film);
        if (films.values().stream().map(Film::getName).anyMatch(film.getName()::equals)) {
            throw new ValidationException("Фильм с названием " + film.getName() + "уже добавлен");
        }
        films.put(film.getId(), film);
        log.debug("Добавление фильма: {}", film);
        return ResponseEntity.status(HttpStatus.OK).body(film);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@RequestBody Film film) {
        checkFilmCriteria(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.debug("Перезапись фильма: {}", film);
            return ResponseEntity.status(HttpStatus.OK).body(film);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(film);
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
        if (film.getId() == 0) {
            film.setId(++genId);
        }
    }

    void deleteAllFilms() {
        films.clear();
    }
}
