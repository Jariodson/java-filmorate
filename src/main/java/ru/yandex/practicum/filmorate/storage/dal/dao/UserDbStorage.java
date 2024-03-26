package ru.yandex.practicum.filmorate.storage.dal.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.Enums.FriendshipStatus;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Status;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    JdbcTemplate jdbcTemplate;

    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<User> getAllUsers() {
        String sql = "SELECT * FROM \"user\"";
        return jdbcTemplate.query(sql, this::makeUser);
    }

    @Override
    public User getUserById(Long id) {
        return jdbcTemplate.queryForObject("SELECT u.* FROM \"user\" AS u " +
                "WHERE u.user_id = ?", this::makeUser, id);

    }

    @Override
    public void addNewUser(User user) {
        checkUserCriteria(user);
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
        if (!checkUserInDb(user.getId())) {
            throw new IllegalArgumentException("Пользователь с ID: " + user.getId() + " не найден в базе данных!");
        }
        checkUserCriteria(user);
        jdbcTemplate.update("UPDATE \"user\" SET email=?, login=?, birthday=?, name=? WHERE user_id=?",
                user.getEmail(), user.getLogin(), user.getBirthday(), user.getName(), user.getId());
    }

    @Override
    public void deleteUser(User user) {
        checkUserCriteria(user);
        if (checkUserInDb(user.getId())) {
            jdbcTemplate.update("DELETE FROM \"user\" WHERE user_id=?", user.getId());
        }
        throw new IllegalArgumentException("Пользователь с ID: " + user.getId() + " не найден в базе данных!");
    }

    @Override
    public Collection<User> getFriends(long userId) {
        if (!checkUserInDb(userId)) {
            throw new IllegalArgumentException("Пользователь с ID: " + userId + " не найден в базе данных!");
        }
        String sql = "SELECT u.*, f.friendUser_id, s.status FROM \"user\" AS u " +
                "JOIN friendship AS f ON u.user_id = f.user_id " +
                "JOIN status AS s ON s.status_id = f.status_id " +
                "WHERE u.user_id = ? AND s.status = 'CONFIRMED'";
        return jdbcTemplate.query(sql, this::makeUser, userId);
    }

    @Override
    public User addFriend(long userId, long friendId) {
        checkUsersIds(userId, friendId);
        Long statusId = jdbcTemplate.queryForObject("SELECT status_id FROM status " +
                "WHERE status = 'CONFIRMED'", Long.class);
        jdbcTemplate.update("INSERT INTO friendship (status_id, user_id, friendUser_id) VALUES (?, ?, ?)",
                statusId, userId, friendId);
        // jdbcTemplate.update("INSERT INTO friendship (status_id, user_id, friendUser_id) VALUES (?, ?, ?)",
        //        statusId, friendId, userId);
        return getUserById(userId);
    }

    @Override
    public User deleteFriend(long userId, long friendId) {
        checkUsersIds(userId, friendId);
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ? AND friendUser_id = ?", userId, friendId);
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ? AND friendUser_id = ?", friendId, userId);
        return getUserById(userId);
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long friendId) {
        checkUsersIds(userId, friendId);
        String sql = "SELECT u.* " +
                "FROM friendship f1 " +
                "JOIN friendship f2 ON f1.friendUser_id = f2.friendUser_id " +
                "JOIN \"user\" AS u ON f1.friendUser_id = u.user_id " +
                "WHERE f1.user_id = ? AND f2.user_id = ?";
        return jdbcTemplate.query(sql, this::makeUser, userId, friendId);
    }

    private boolean checkUserInDb(Long id) {
        String sql = "SELECT COUNT(*) FROM \"user\" WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count != 0;
    }

    private void checkUsersIds(long userId, long friendId) {
        if (!checkUserInDb(userId)) {
            throw new IllegalArgumentException("Пользователь с ID: " + userId + " не найден в базе данных!");
        }
        if (!checkUserInDb(friendId)) {
            throw new IllegalArgumentException("Пользователь с ID: " + friendId + " не найден в базе данных!");
        }
    }

    private User makeUser(ResultSet rs, int rowNum) throws SQLException {
        User user = User.builder()
                .id(rs.getLong("user_id"))
                .email(rs.getString("email"))
                .name(rs.getString("name"))
                .birthday(rs.getDate("birthday").toLocalDate())
                .login(rs.getString("login"))
                .build();

        /*
        String sql = "SELECT f.friendUser_id, f.status_id, s.status " +
                "FROM friendship AS f " +
                "JOIN status AS s ON s.status_id = f.status_id " +
                "WHERE f.user_id = ?";

        Map<Long, Status> friendStatusMap = jdbcTemplate.query(sql, rs1 -> {
            Map<Long, Status> resultMap = new HashMap<>();
            while (rs1.next()) {
                Long friendId = rs1.getLong("friendUser_id");
                Status status = new Status();
                status.setId(rs1.getLong("status_id"));
                status.setStatus(converter(rs1.getString("status")));
                resultMap.put(friendId, status);
            }
            return resultMap;
        }, user.getId());
        if (friendStatusMap != null) {
            user.getFriendsIds().putAll(friendStatusMap);
        }

         */
        return user;
    }

    /*
    private FriendshipStatus converter(String status) {
        switch (status) {
            case "CONFIRMED":
                return FriendshipStatus.CONFIRMED;
            case "UNCONFIRMED":
                return FriendshipStatus.UNCONFIRMED;
            default:
                throw new IllegalArgumentException("Неверный статус! Статус: " + status);
        }
    }

     */

    private void checkUserCriteria(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Введено пустое имя, поэтому имя изменено на логин: {}", user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now().plusDays(1))) {
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
