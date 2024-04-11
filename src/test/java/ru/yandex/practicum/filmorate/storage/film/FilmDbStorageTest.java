package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.dal.*;
import ru.yandex.practicum.filmorate.storage.dal.dao.*;
import ru.yandex.practicum.filmorate.storage.mapper.DirectorMapper;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.storage.mapper.GenreMapper;
import ru.yandex.practicum.filmorate.storage.mapper.MpaMapper;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorageTest;

import java.time.LocalDate;
import java.util.*;

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
    Film film;
    Film secondFilm;
    User user;
    User secondUser;



    @BeforeEach
    void beforeEach() {
        likeStorage = new LikeDao(jdbcTemplate);
        genreDal = new GenreDao(jdbcTemplate);
        directorDal = new DirectorDao(jdbcTemplate);
        filmStorage = new FilmDbStorage(jdbcTemplate, genreDal, likeStorage, directorDal, filmMapper);
        userStorage = new UserDbStorage(jdbcTemplate);
        filmMapper = new FilmMapper(jdbcTemplate, new MpaMapper(), new GenreMapper(), new DirectorMapper());
        film = createFilm();
        secondFilm = createSecondFilm();
        user = UserDbStorageTest.createUser(1);
        secondUser = UserDbStorageTest.createUser(2);

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


    private Film createFilm() {
        return Film.builder()
                .name("Pulp Fiction")
                .description("The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption.")
                .duration(154)
                .releaseDate(LocalDate.of(1994, 10, 14))
                .mpa(Mpa.builder().id(4L).name("R").build())
                .genres(Set.of(
                        Genre.builder().id(1L).name("Crime").build(),
                        Genre.builder().id(2L).name("Drama").build(),
                        Genre.builder().id(6L).name("Thriller").build()
                ))
                .directors(Set.of(
                        Director.builder().id(1L).name("Quentin Tarantino").build(),
                        Director.builder().id(2L).name("Tony Scott").build()
                ))
                .build();
    }

    private Film createSecondFilm() {
        return Film.builder()
                .name("Inception")
                .description("A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.")
                .duration(148)
                .releaseDate(LocalDate.of(2010, 7, 16))
                .mpa(Mpa.builder().id(4L).name("R").build())
                .genres(Set.of(
                        Genre.builder().id(2L).name("Drama").build(),
                        Genre.builder().id(3L).name("Mystery").build(),
                        Genre.builder().id(6L).name("Thriller").build()
                ))
                .directors(Set.of(
                        Director.builder().id(3L).name("Christopher Nolan").build()
                ))
                .build();
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

    @Test
    public void testSearchFilmByParameter() {

        FilmDbStorage filmDbStorage = new FilmDbStorage(jdbcTemplate, null, null, null, filmMapper);

        String query = "Some query";
        String filmSearchParameter = "title";

        Collection<Film> films = filmDbStorage.searchFilmByParameter(query, filmSearchParameter);

        assertThat(films).isNotNull();

    }


}