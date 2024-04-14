package ru.yandex.practicum.filmorate.storage.dal.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.*;
import ru.yandex.practicum.filmorate.storage.database.*;
import ru.yandex.practicum.filmorate.storage.database.DbGenreStorage;
import ru.yandex.practicum.filmorate.storage.mapper.FilmMapper;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
class DbReviewStorageTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    private ReviewStorage reviewDao;
    private Review review;
    private ru.yandex.practicum.filmorate.storage.GenreStorage genreStorage;
    private LikeStorage likeStorage;
    private DirectorStorage directorStorage;
    private FilmMapper filmMapper;


    @BeforeEach
    void beforeEach() {
        reviewDao = new DbReviewStorage(jdbcTemplate);
        likeStorage = new DbLikeStorage(jdbcTemplate);
        genreStorage = new DbGenreStorage(jdbcTemplate);
        directorStorage = new DbDirectorStorage(jdbcTemplate);
        FilmStorage filmStorage = new DbFilmStorage(jdbcTemplate, filmMapper);
        UserStorage userStorage = new DbUserStorage(jdbcTemplate);

        User user = User.builder()
                .id(1L)
                .email("user@email.ru")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .login("vanya123")
                .build();
        userStorage.addNewUser(user);

        Film film = Film.builder()
                .name("Крестный отец")
                .description("Итальянская мафия в США")
                .releaseDate(LocalDate.of(1972, 3, 15))
                .duration(175)
                .mpa(Mpa.builder().id(5L).name("NC-17").build())
                .genres(Set.of(Genre.builder().name("Боевик").id(6L).build(),
                        Genre.builder().id(2L).name("Драма").build()))
                .build();
        filmStorage.addNewFilm(film);

        review = Review.builder()
                .reviewId(1L)
                .content("This film is sooo baad.")
                .isPositive(false)
                .userId(1L)
                .filmId(1L)
                .build();
        reviewDao.makeReview(review);
    }

    @Test
    void makeReview() {
        Review newReview = reviewDao.getReviewById(1L);
        assertThat(newReview).isNotNull().isEqualTo(review);
    }

    @Test
    void updateReview() {
        Review newReview = Review.builder()
                .reviewId(1L)
                .content("This film is sooo cool!.")
                .isPositive(true)
                .userId(1L)
                .filmId(1L)
                .build();
        reviewDao.updateReview(newReview);
        assertThat(newReview).isNotNull().isEqualTo(reviewDao.getReviewById(1L));
    }

    @Test
    void deleteReview() {
        reviewDao.deleteReview(1L);
        assertThat(reviewDao.getReviewByFilmId(1L, 10).size()).isEqualTo(0);
    }

    @Test
    void getReviewById() {
        assertThat(review).isEqualTo(reviewDao.getReviewById(1L));
    }

    @Test
    void getReviewByFilmId() {
        Collection<Review> reviews = reviewDao.getReviewByFilmId(1L, 10);
        assertThat(reviews.size()).isEqualTo(1);
    }

    @Test
    void postLike() {
        reviewDao.postLike(1L, 1L);
        assertThat(reviewDao.getReviewById(1L).getUseful()).isEqualTo(1);
    }

    @Test
    void postDislike() {
        reviewDao.postDislike(1L, 1L);
        assertThat(reviewDao.getReviewById(1L).getUseful()).isEqualTo(-1);
    }

    @Test
    void deleteLike() {
        reviewDao.postLike(1L, 1L);
        assertThat(reviewDao.getReviewById(1L).getUseful()).isEqualTo(1);

        reviewDao.deleteLike(1L, 1L);
        assertThat(reviewDao.getReviewById(1L).getUseful()).isEqualTo(0);
    }

    @Test
    void deleteDislike() {
        reviewDao.postDislike(1L, 1L);
        assertThat(reviewDao.getReviewById(1L).getUseful()).isEqualTo(-1);

        reviewDao.deleteDislike(1L, 1L);
        assertThat(reviewDao.getReviewById(1L).getUseful()).isEqualTo(0);
    }
}