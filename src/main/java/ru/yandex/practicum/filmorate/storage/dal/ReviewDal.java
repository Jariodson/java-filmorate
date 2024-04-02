package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.Collection;

public interface ReviewDal {
    void makeReview(Review review);

    void updateReview(Review review);

    void deleteReview(Long id);

    Review getReviewById(Long id);

    Collection<Review> getReviewByFilmId(Long filmId, int count);

    Collection<Review> getAllReviews(int count);

    void postLike(Long id, Long userId);

    void postDislike(Long id, Long userId);

    void deleteLike(Long id, Long userId);

    void deleteDislike(Long id, Long userId);
}
