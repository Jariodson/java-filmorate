package ru.yandex.practicum.filmorate.storage.database;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Slf4j
@Repository
@Qualifier("userDbStorage")
public class DbUserStorage implements UserStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM \"user\"";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    @Override
    public User getUserById(Long id) {
        return jdbcTemplate.queryForObject("SELECT * FROM \"user\" WHERE user_id = ?", this::makeUser, id);
    }

    @Override
    public void addNewUser(User user) {
        SimpleJdbcInsert jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("\"user\"")
                .usingGeneratedKeyColumns("user_id")
                .usingColumns("email", "login", "birthday", "name");
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("email", user.getEmail());
        parameters.put("login", user.getLogin());
        parameters.put("birthday", user.getBirthday());
        parameters.put("name", user.getName());

        Long id = jdbcInsert.executeAndReturnKey(parameters).longValue();
        user.setId(id);
    }

    @Override
    public void updateUser(User user) {
        jdbcTemplate.update("UPDATE \"user\" SET email=?, login=?, birthday=?, name=? WHERE user_id=?",
                user.getEmail(), user.getLogin(), user.getBirthday(), user.getName(), user.getId());
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ?", user.getId());
    }

    @Override
    public void deleteUser(Long id) {
        jdbcTemplate.update("DELETE FROM \"user\" WHERE user_id = ?", id);
    }

    @Override
    public Collection<User> getFriends(long userId) {
        String sql = "SELECT u.* FROM \"user\" AS u " +
                "JOIN friendship AS f ON f.FRIENDUSER_ID = u.user_id " +
                "WHERE f.user_id = ?";
        return jdbcTemplate.query(sql, this::makeUser, userId);
    }

    @Override
    public User addFriend(long userId, long friendId) {
        jdbcTemplate.update("INSERT INTO friendship (user_id, friendUser_id) VALUES (?, ?)", userId, friendId);
        return getUserById(userId);
    }

    @Override
    public User deleteFriend(long userId, long friendId) {
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ? AND friendUser_id = ?", userId, friendId);
        return getUserById(userId);
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long friendId) {
        String sql = "SELECT u.* " +
                "FROM friendship f1 " +
                "JOIN friendship f2 ON f1.FRIENDUSER_ID = f2.FRIENDUSER_ID " +
                "JOIN \"user\" AS u ON f1.FRIENDUSER_ID = u.user_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.query(sql, this::makeUser, userId, friendId);
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .login(rs.getString("login"))
                .build();

        String sql = "SELECT friendUser_id FROM friendship WHERE user_id = ?";
        List<Long> friendIds = jdbcTemplate.queryForList(sql, Long.class, user.getId());
        Set<Long> friends = new HashSet<>(friendIds);
        user.setFriendsIds(friends);
        return user;
    }
}
