package ru.yandex.practicum.filmorate.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.dal.ReviewDal;

import java.util.Collection;

@Service
public class ReviewServiceImpl implements ReviewService {
    private final ReviewDal reviewDao;

    private final UserService userService;
    private final FilmService filmService;

    @Autowired
    public ReviewServiceImpl(ReviewDal reviewDao, UserService userService, FilmService filmService) {
        this.reviewDao = reviewDao;
        this.userService = userService;
        this.filmService = filmService;
    }

    @Override
    public void createReview(Review review) {
        checkReview(review);
        reviewDao.makeReview(review);
    }

    @Override
    public void updateReview(Review review) {
        checkReview(review);
        reviewDao.updateReview(review);
    }

    @Override
    public Review deleteReview(Long id) {
        Review review = reviewDao.getReviewById(id);
        reviewDao.deleteReview(id);
        return review;
    }

    @Override
    public Review getReviewById(Long id) {
        Review review;
        try {
            review = reviewDao.getReviewById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Отзыва с ID: " + id + "не найдено!");
        }
        return review;
    }

    @Override
    public Collection<Review> getReviewByFilmId(Long filmId, int count) {
        filmService.getFilmById(filmId);
        Collection<Review> reviews;
        try {
            reviews = reviewDao.getReviewByFilmId(filmId, count);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Отзывов на фильм с ID: " + filmId + " не найдено!");
        }
        return reviews;
    }

    @Override
    public Collection<Review> getAllReviews(int count) {
        return reviewDao.getAllReviews(count);
    }

    @Override
    public Review postLike(Long id, Long userId) {
        userService.getUserById(userId);
        reviewDao.postLike(id, userId);
        return reviewDao.getReviewById(id);
    }

    @Override
    public Review postDislike(Long id, Long userId) {
        reviewDao.postDislike(id, userId);
        return reviewDao.getReviewById(id);
    }

    @Override
    public Review deleteLike(Long id, Long userId) {
        reviewDao.deleteLike(id, userId);
        return reviewDao.getReviewById(id);
    }

    @Override
    public Review deleteDislike(Long id, Long userId) {
        reviewDao.deleteDislike(id, userId);
        return reviewDao.getReviewById(id);
    }

    private void checkReview(Review review) {
        userService.getUserById(review.getUserId());
        filmService.getFilmById(review.getFilmId());
        if (review.getUserId() <= 0 || review.getFilmId() <= 0) {
            throw new IllegalArgumentException("ID не может быть отрицательным или равняться 0!");
        }
        if (review.getIsPositive() == null) {
            throw new NotFoundException("isPositive = null!");
        }
    }
}
