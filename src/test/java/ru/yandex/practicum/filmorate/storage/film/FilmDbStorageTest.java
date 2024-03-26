package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.FilmStorage;
import ru.yandex.practicum.filmorate.storage.dal.dao.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.dao.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.dal.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class FilmDbStorageTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private FilmStorage filmStorage;
    private UserStorage userStorage;

    @BeforeEach
    void beforeEach() {
        filmStorage = new FilmDbStorage(jdbcTemplate);
        userStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    void getAllFilms() {
        Film film1 = Film.builder()
                .name("Крестный отец")
                .description("Итальянская мафия в США")
                .releaseDate(LocalDate.of(1972, 3, 15))
                .duration(175)
                .mpa(Mpa.builder().id(5L).name("NC-17").build())
                .genres(Set.of(Genre.builder().name("Боевик").id(6L).build(),
                        Genre.builder().id(2L).name("Драма").build()))
                .build();
        filmStorage.addNewFilm(film1);
        assertThat(filmStorage.getAllFilms()).isNotNull();
    }

    @Test
    void getFilmById() {
        Film film1 = Film.builder()
                .name("Крестный отец")
                .description("Итальянская мафия в США")
                .releaseDate(LocalDate.of(1972, 3, 15))
                .duration(175)
                .mpa(Mpa.builder().id(5L).name("NC-17").build())
                .genres(Set.of(Genre.builder().name("Боевик").id(6L).build(),
                        Genre.builder().id(2L).name("Драма").build()))
                .build();
        filmStorage.addNewFilm(film1);
        assertThat(filmStorage.getFilmById(1L))
                .isNotNull();
    }

    @Test
    void addNewFilm() {
        Film film1 = Film.builder()
                .name("Крестный отец")
                .description("Итальянская мафия в США")
                .releaseDate(LocalDate.of(1972, 3, 15))
                .duration(175)
                .mpa(Mpa.builder().id(5L).name("NC-17").build())
                .genres(Set.of(Genre.builder().name("Боевик").id(6L).build(),
                        Genre.builder().id(2L).name("Драма").build()))
                .build();
        filmStorage.addNewFilm(film1);
        assertThat(filmStorage.getFilmById(1L)).isNotNull();
    }

    @Test
    void updateFilm() {
        Film film1 = Film.builder()
                .name("Крестный отец")
                .description("Итальянская мафия в США")
                .releaseDate(LocalDate.of(1972, 3, 15))
                .duration(175)
                .mpa(Mpa.builder().id(5L).name("NC-17").build())
                .genres(Set.of(Genre.builder().name("Боевик").id(6L).build(),
                        Genre.builder().id(2L).name("Драма").build()))
                .build();
        filmStorage.addNewFilm(film1);
        Film film2 = Film.builder()
                .id(1L)
                .name("Папа Дон")
                .description("Итальянская мафия в Италии")
                .releaseDate(LocalDate.of(1972, 3, 15))
                .duration(175)
                .mpa(Mpa.builder().id(2L).name("PG").build())
                .genres(Set.of(Genre.builder().name("Боевик").id(6L).build(),
                        Genre.builder().id(1L).name("Комедия").build()))
                .build();
        filmStorage.updateFilm(film2);

        Film newFilm = filmStorage.getFilmById(1L);
        assertThat(newFilm).isNotNull();
    }

    @Test
    void deleteFilm() {
        Film film1 = Film.builder()
                .name("Крестный отец")
                .description("Итальянская мафия в США")
                .releaseDate(LocalDate.of(1972, 3, 15))
                .duration(175)
                .mpa(Mpa.builder().id(5L).name("NC-17").build())
                .genres(Set.of(Genre.builder().name("Боевик").id(6L).build(),
                        Genre.builder().id(2L).name("Драма").build()))
                .build();
        filmStorage.addNewFilm(film1);
        assertThat(filmStorage.getFilmById(1L)).isNotNull();
        filmStorage.deleteFilm(film1);
        assertThat(filmStorage.getAllFilms()).isNotNull().isEqualTo(Collections.EMPTY_LIST);
    }

    @Test
    void getFavouriteFilms() {
        Film film1 = Film.builder()
                .name("Крестный отец")
                .description("Итальянская мафия в США")
                .releaseDate(LocalDate.of(1972, 3, 15))
                .duration(175)
                .mpa(Mpa.builder().id(5L).name("NC-17").build())
                .genres(Set.of(Genre.builder().name("Боевик").id(6L).build(),
                        Genre.builder().id(2L).name("Драма").build()))
                .build();
        filmStorage.addNewFilm(film1);

        User newUser = User.builder()
                .email("user@email.ru")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .login("vanya123")
                .build();
        userStorage.addNewUser(newUser);

        filmStorage.addLike(film1.getId(), newUser.getId());

        Collection<Film> films = filmStorage.getFavouriteFilms(10);
        assertThat(films).isNotNull();
    }

    @Test
    void addLike() {
        Film film1 = Film.builder()
                .name("Крестный отец")
                .description("Итальянская мафия в США")
                .releaseDate(LocalDate.of(1972, 3, 15))
                .duration(175)
                .mpa(Mpa.builder().id(5L).name("NC-17").build())
                .genres(Set.of(Genre.builder().name("Боевик").id(6L).build(),
                        Genre.builder().id(2L).name("Драма").build()))
                .build();
        filmStorage.addNewFilm(film1);

        User newUser = User.builder()
                .email("user@email.ru")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .login("vanya123")
                .build();
        userStorage.addNewUser(newUser);

        Film film = filmStorage.addLike(film1.getId(), newUser.getId());
        assertThat(film).isNotNull();
    }

    @Test
    void removeLike() {
        Film film1 = Film.builder()
                .name("Крестный отец")
                .description("Итальянская мафия в США")
                .releaseDate(LocalDate.of(1972, 3, 15))
                .duration(175)
                .mpa(Mpa.builder().id(5L).name("NC-17").build())
                .genres(Set.of(Genre.builder().name("Боевик").id(6L).build(),
                        Genre.builder().id(2L).name("Драма").build()))
                .build();
        filmStorage.addNewFilm(film1);

        User newUser = User.builder()
                .email("user@email.ru")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .login("vanya123")
                .build();
        userStorage.addNewUser(newUser);

        Film film = filmStorage.addLike(film1.getId(), newUser.getId());
        assertThat(film).isNotNull();

        Film film2 = filmStorage.removeLike(film.getId(), newUser.getId());
        assertThat(film2).isNotNull();
    }
}