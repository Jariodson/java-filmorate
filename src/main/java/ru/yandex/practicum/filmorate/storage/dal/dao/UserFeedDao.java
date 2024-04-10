package ru.yandex.practicum.filmorate.storage.dal.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.storage.dal.UserFeedDal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Slf4j
@Repository
public class UserFeedDao implements UserFeedDal {

    private final JdbcTemplate jdbcTemplate;

    public UserFeedDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }


    @Override
    public void addUserFeed(UserFeed feed) {
        String sql = "INSERT INTO user_feed (instant, user_id, event_type, operation, entity_id) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, feed.getTimestamp(), feed.getUserId(), feed.getEventType(),
                feed.getOperation(), feed.getEntityId());
    }

    @Override
    public Collection<UserFeed> getUserFeed(Long userId) {
        String sql = "SELECT * FROM user_feed WHERE user_id = ?";
        return jdbcTemplate.query(sql, this::makeFeed, userId);
    }

    private UserFeed makeFeed(ResultSet rs, int rowNum) throws SQLException {
        return UserFeed.builder()
                .eventId(rs.getLong("event_id"))
                .timestamp(rs.getTimestamp("instant").toInstant())
                .userId(rs.getLong("user_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .entityId(rs.getLong("entity_id"))
                .build();
    }

}
