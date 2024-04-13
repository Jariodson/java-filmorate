package ru.yandex.practicum.filmorate.storage.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class ReviewDao implements ReviewDal {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ReviewDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void makeReview(Review review) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("review")
                .usingGeneratedKeyColumns("review_id")
                .usingColumns("content", "is_positive", "user_id", "film_id");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("content", review.getContent());
        parameters.put("is_positive", review.getIsPositive());
        parameters.put("user_id", review.getUserId());
        parameters.put("film_id", review.getFilmId());
        Long id = jdbcInsert.executeAndReturnKey(parameters).longValue();
        review.setReviewId(id);
    }

    @Override
    public void updateReview(Review review) {
        String sql = "UPDATE review SET " +
                "content = ?, " +
                "is_positive = ? " +
                "WHERE review_id = ?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getReviewId());
    }

    @Override
    public void deleteReview(Long id) {
        String sql1 = "DELETE FROM review_likes WHERE review_id = ?";
        jdbcTemplate.update(sql1, id);
        String sql2 = "DELETE FROM review_dislikes WHERE review_id = ?";
        jdbcTemplate.update(sql2, id);
        String sql = "DELETE FROM review WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
    }

    @Override
    public Review getReviewById(Long id) {
        String sql = "SELECT * FROM review WHERE review_id = ?";
        return jdbcTemplate.queryForObject(sql, this::buildReview, id);
    }

    @Override
    public Collection<Review> getReviewByFilmId(Long filmId, int count) {
        String sql = "SELECT * FROM review " +
                "WHERE film_id = ? " +
                "LIMIT ?; ";
        Collection<Review> reviews = jdbcTemplate.query(sql, this::buildReview, filmId, count);
        return reviews.stream().sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public Collection<Review> getAllReviews(int count) {
        String sql = "SELECT * FROM review " +
                "LIMIT ?";
        Collection<Review> reviews = jdbcTemplate.query(sql, this::buildReview, count);
        return reviews.stream().sorted(Comparator.comparingInt(Review::getUseful).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public void postLike(Long id, Long userId) {
        String sql = "INSERT INTO review_likes VALUES (?, ?)";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void postDislike(Long id, Long userId) {
        String sql = "INSERT INTO review_dislikes VALUES (?, ?)";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void deleteLike(Long id, Long userId) {
        String sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
    }

    @Override
    public void deleteDislike(Long id, Long userId) {
        String sql = "DELETE FROM review_dislikes WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, id, userId);
    }

    private Review buildReview(ResultSet rs, int rowNum) throws SQLException {
        Review review = Review.builder()
                .reviewId(rs.getLong("review_id"))
                .content(rs.getString("content"))
                .isPositive(rs.getBoolean("is_positive"))
                .userId(rs.getLong("user_id"))
                .filmId(rs.getLong("film_id"))
                .build();
        review.setUseful(updateUseful(review.getReviewId()));
        return review;
    }

    private Integer updateUseful(Long reviewId){
        String sqlFromLikes = "SELECT COUNT(*) FROM review_likes WHERE review_id = ?";
        Integer countLikes = jdbcTemplate.queryForObject(sqlFromLikes, Integer.class, reviewId);
        String sqlFromDislikes = "SELECT COUNT(*) FROM review_dislikes WHERE review_id = ?";
        Integer countDislikes = jdbcTemplate.queryForObject(sqlFromDislikes, Integer.class, reviewId);
        if (countLikes != null && countDislikes != null) {
            return countLikes - countDislikes;
        }
        return 0;
    }
}
