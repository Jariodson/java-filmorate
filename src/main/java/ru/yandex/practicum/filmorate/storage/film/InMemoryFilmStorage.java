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
        log.info("Получен запрос GET на получение списка всех фильмов");
        log.info("Размер списка фильмов: {}", films.size());
        return films.values();
    }

    @Override
    public void addNewFilm(Film film) {
        log.info("Получен запрос POST на добавление фильма в список");
        checkFilmCriteria(film);
        if (films.values().stream().map(Film::getName).anyMatch(film.getName()::equals)) {
            log.warn("Фильм с названием {} уже добавлен", film.getName());
            throw new ValidationException("Фильм с названием " + film.getName() + " уже добавлен");
        }
        films.put(film.getId(), film);
        log.info("Фильм добавлен в список: {}.\nРазмер списка: {}", film, films.size());
    }

    @Override
    public void updateFilm(Film film) {
        log.info("Получен запрос PUT на обновления фильма в списке");
        checkFilmCriteria(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
            log.info("Обновленный фильм: {} добавлен в список. Размер списка: {}", film, films.size());
            return;
        }
        log.warn("Фильм не содержится в списке!");
        throw new IllegalArgumentException("Такого фильма не существует!");
    }

    @Override
    public void deleteFilm(Film film) {
        log.info("Получен запрос DELETE на удаление фильма");
        if (!films.containsValue(film)){
            log.warn("Ошибка! Фильма {} не существует!", film);
            throw new IllegalArgumentException("Такого фильма не сушествует!");
        }
        films.remove(film.getId());
        log.info("Фильм удалён! {}", film);
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
