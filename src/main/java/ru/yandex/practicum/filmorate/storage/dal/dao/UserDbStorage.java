package ru.yandex.practicum.filmorate.storage.dal.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.UserStorage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Slf4j
@Repository
@Qualifier("userDbStorage")
public class UserDbStorage implements UserStorage {
    JdbcTemplate jdbcTemplate;

    @Autowired
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
        return jdbcTemplate.queryForObject("SELECT * FROM \"user\" WHERE user_id = ?", this::makeUser, id);
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
        if (checkUserInDb(user.getId())) {
            throw new IllegalArgumentException("Пользователь с ID: " + user.getId() + " не найден в базе данных!");
        }
        checkUserCriteria(user);
        jdbcTemplate.update("UPDATE \"user\" SET email=?, login=?, birthday=?, name=? WHERE user_id=?",
                user.getEmail(), user.getLogin(), user.getBirthday(), user.getName(), user.getId());
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ?", user.getId());

        /*
        if (user.getFriendsIds() != null && !user.getFriendsIds().isEmpty()) {
            user.getFriendsIds().forEach(
                    id -> jdbcTemplate.update("INSERT INTO friendship (user_id, friendUser_id)" +
                    " VALUES (?, ?)", user.getId(), id));
        }

         */
    }

    @Override
    public void deleteUser(User user) {
        checkUserCriteria(user);
        if (checkUserInDb(user.getId())) {
            throw new IllegalArgumentException("Пользователь с ID: " + user.getId() + " не найден в базе данных!");
        }
        jdbcTemplate.update("DELETE FROM \"user\" WHERE user_id = ?", user.getId());
    }

    @Override
    public Collection<User> getFriends(long userId) {
        if (checkUserInDb(userId)) {
            throw new IllegalArgumentException("Пользователь с ID: " + userId + " не найден в базе данных!");
        }
        String sql = "SELECT friendUser_id FROM friendship WHERE user_id = ?";
        //return jdbcTemplate.query(sql, (rs, rowNum) -> rs.getLong("friendUser_id"), userId);
        //return jdbcTemplate.queryForObject(sql, Long.class, userId);
        sql = "SELECT u.* FROM \"user\" AS u " +
                "JOIN friendship AS f ON f.user_id = u.user_id " +
                "WHERE friendUser_id = ?";
        return jdbcTemplate.query(sql, this::makeUser, userId);
    }

    @Override
    public User addFriend(long userId, long friendId) {
        checkUsersIds(userId, friendId);
        jdbcTemplate.update("INSERT INTO friendship (user_id, friendUser_id) VALUES (?, ?)", userId, friendId);
        return getUserById(userId);
    }

    @Override
    public User deleteFriend(long userId, long friendId) {
        checkUsersIds(userId, friendId);
        jdbcTemplate.update("DELETE FROM friendship WHERE user_id = ? AND friendUser_id = ?", userId, friendId);
        return getUserById(userId);
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long friendId) {
        checkUsersIds(userId, friendId);
        String sql = "SELECT u.* " +
                "FROM friendship f1 " +
                "JOIN friendship f2 ON f1.user_id = f2.user_id " +
                "JOIN \"user\" AS u ON f1.user_id = u.user_id " +
                "WHERE f1.friendUser_id = ? AND f2.friendUser_id = ?";
        List<User> users = jdbcTemplate.query(sql, this::makeUser, userId, friendId);
        return users;
    }

    private boolean checkUserInDb(Long id) {
        String sql = "SELECT COUNT(*) FROM \"user\" WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count == null || count == 0;
    }

    private void checkUsersIds(long userId, long friendId) {
        if (checkUserInDb(userId)) {
            throw new IllegalArgumentException("Пользователь с ID: " + userId + " не найден в базе данных!");
        }
        if (checkUserInDb(friendId)) {
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

        String sql = "SELECT friendUser_id FROM friendship WHERE user_id = ?";
        List<Long> friendIds = jdbcTemplate.queryForList(sql, Long.class, user.getId());
        Set<Long> friends = new HashSet<>(friendIds);
        user.setFriendsIds(friends);

        //user.setFriendsIds(new HashSet<>(jdbcTemplate.query(sql,
        //        (rs1, rowNum1) -> rs.getLong("friendUser_id"), user.getId())));
        return user;
    }

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
