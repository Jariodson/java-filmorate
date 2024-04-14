package ru.yandex.practicum.filmorate.storage.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.storage.UserFeedStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

@Slf4j
@Repository
public class DbUserFeedStorage implements UserFeedStorage {

    private final JdbcTemplate jdbcTemplate;

    public DbUserFeedStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void addUserFeed(UserFeed feed) {
        String sql = "INSERT INTO user_feed ( user_id, entity_id,instant, event_type, operation) " +
                "VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, feed.getUserId(), feed.getEntityId(), feed.getTimestamp(),
                feed.getEventType().toString(), feed.getOperation().toString());
    }

    @Override
    public Collection<UserFeed> getUserFeed(Long userId) {
        String sql = "SELECT * FROM user_feed WHERE user_id = ?";
        return jdbcTemplate.query(sql, this::makeFeed, userId);
    }

    private UserFeed makeFeed(ResultSet rs, int rowNum) throws SQLException {
        return UserFeed.builder()
                .eventId(rs.getLong("event_id"))
                .userId(rs.getLong("user_id"))
                .entityId(rs.getLong("entity_id"))
                .timestamp(rs.getTimestamp("instant").toInstant())
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operation(Operation.valueOf(rs.getString("operation")))
                .build();
    }
}
