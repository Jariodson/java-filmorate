package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
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
        log.info("Получен запрос GET на получение списка всех фильмов");
        log.info("Размер списка фильмов: {}", films.size());
        return films.values();
    }

    @PostMapping
    public ResponseEntity<Film> addFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос POST на добавление фильма в список");
        checkFilmCriteria(film);
        if (films.values().stream().map(Film::getName).anyMatch(film.getName()::equals)) {
            log.warn("Фильм с названием {} уже добавлен", film.getName());
            throw new ValidationException("Фильм с названием " + film.getName() + "уже добавлен");
        }
        films.put(film.getId(), film);
        log.info("Фильм добавлен в список: {}.\nРазмер списка: {}", film, films.size());
        return ResponseEntity.status(HttpStatus.OK).body(film);
    }

    @PutMapping
    public ResponseEntity<Film> updateFilm(@Valid @RequestBody Film film) {
        log.info("Получен запрос PUT на обновления фильма в списке");
        checkFilmCriteria(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Обновленный фильм: {} добавлен в список.\nРазмер списка: {}", film, films.size());
            return ResponseEntity.status(HttpStatus.OK).body(film);
        }
        log.warn("Фильм не содержится в списке!");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(film);
    }

    private void checkFilmCriteria(Film film) {
        LocalDate filmBirthday = LocalDate.of(1895, 12, 28);
        if (film.getReleaseDate().isBefore(filmBirthday)) {
            log.warn("Введена слишком ранняя дата релиза: {}", film.getReleaseDate());
            throw new ValidationException("Слшиком ранняя дата релиза!");
        }
        if (film.getId() == 0) {
            film.setId(++genId);
        }
    }
}
