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
import ru.yandex.practicum.filmorate.storage.dal.*;
import ru.yandex.practicum.filmorate.storage.dal.dao.*;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.time.LocalDate;
import java.util.ArrayList;
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
    private GenreDal genreDal;
    private LikeDal likeStorage;
    private DirectorDal directorDal;
    private FilmMapper filmMapper;

    @BeforeEach
    void beforeEach() {
        likeStorage = new LikeDao(jdbcTemplate);
        genreDal = new GenreDao(jdbcTemplate);
        directorDal = new DirectorDao(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, genreDal, likeStorage, directorDal, filmMapper);
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
        filmStorage.deleteFilm(film1.getId());
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

        // filmStorage.addLike(film1.getId(), newUser.getId());

        Collection<Film> films = filmStorage.getMostPopularsFilms(10, null, null);
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
        likeStorage.addLike(film1.getId(), newUser.getId());
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

        likeStorage.addLike(film1.getId(), newUser.getId());

    }

    @Test
    void getCommonFilms() {
        Film film1 = Film.builder()
                .name("Крестный отец")
                .description("Итальянская мафия в США")
                .releaseDate(LocalDate.of(1972, 3, 15))
                .duration(175)
                .mpa(Mpa.builder().id(5L).name("NC-17").build())
                .genres(Set.of(Genre.builder().name("Боевик").id(6L).build(),
                        Genre.builder().id(2L).name("Драма").build()))
                .build();
        film1.setLikes(Set.of(1L, 2L));
        filmStorage.addNewFilm(film1);

        Collection<Film> commonFilms = new ArrayList<>();
        commonFilms.add(film1);

        User newUser = User.builder()
                .email("user@email.ru")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .login("vanya123")
                .build();
        userStorage.addNewUser(newUser);
        User newUser1 = User.builder()
                .email("user1@email.ru")
                .name("Ivan1 Petrov1")
                .birthday(LocalDate.of(1990, 1, 1))
                .login("vanya1231")
                .build();
        userStorage.addNewUser(newUser1);
        likeStorage.addLike(1L, 1L);
        likeStorage.addLike(1L, 2L);
        Collection<Film> savedFilmsCommon = filmStorage.getCommonFilms(1L, 2L);
    }

}