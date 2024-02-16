package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;

import java.rmi.server.RemoteRef;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FilmService {
    FilmStorage filmStorage = new InMemoryFilmStorage();
    public Collection<Film> getFilms(){
        return filmStorage.getAllFilms();
    }
    public Film getFilmById(Long id){
        for (Film film : filmStorage.getAllFilms()){
            if (id.equals(film.getId())){
                return film;
            }
        }
        throw new IllegalArgumentException("Фильма с ID " + id + "не существует!");
    }
    public Collection<Film> getFavouriteFilms(int count){
        log.info("Получен запрос GET на получение самых популярных фильмов!");
        Film[] favouriteFilms = new Film[count];
        List<Film> films = (List<Film>) filmStorage.getAllFilms();
        films.sort(Comparator.comparingInt(o -> o.getLikes().size()));
        for (int i = 0; i < favouriteFilms.length; i++) {
            favouriteFilms[i] = films.get(i);
        }
        log.info("Вывод {} самых популярных фильмов. Id фильмов: {}", count,
                Arrays.stream(favouriteFilms).map(Film::getId).collect(Collectors.toList()));
        return List.of(favouriteFilms);
    }

    public ResponseEntity<Film> addFilm(Film film){
        filmStorage.addNewFilm(film);
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }
    public ResponseEntity<Film> addLike(Long filmId, Long userId){
        Film film = getFilmById(filmId);
        log.info("Получен запрос POST на добавление лайка");
        film.addLike(userId);
        log.info("Лайк успешно поставлен! Id пользователя: {}", userId);
        return new ResponseEntity<>(film, HttpStatus.CREATED);
    }
    public ResponseEntity<Film> updateFilm(Film film){
        filmStorage.updateFilm(film);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }
    public ResponseEntity<Film> deleteFilm(Film film){
        filmStorage.deleteFilm(film);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }

    public ResponseEntity<Film> removeLike(Long filmId, Long userId){
        log.info("Получен запрос DELETE на удаление лайка");
        Film film = getFilmById(filmId);
        film.removeLike(userId);
        log.info("Лайк пользователя {} успешно удалён!", userId);
        return new ResponseEntity<>(film, HttpStatus.OK);
    }
}
