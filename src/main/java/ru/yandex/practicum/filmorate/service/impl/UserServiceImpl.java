package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFeed;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.UserFeedStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.time.Instant;
import java.util.Collection;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserFeedStorage feedStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("dbUserStorage") UserStorage userStorage, UserFeedStorage feedStorage) {
        this.userStorage = userStorage;
        this.feedStorage = feedStorage;
    }

    @Override
    public Collection<User> getUsers() {
        return userStorage.getAllUsers();
    }

    @Override
    public User createUser(User user) {
        checkUserCriteria(user);
        userStorage.addNewUser(user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        checkUserCriteria(user);
        validateUserId(user.getId());
        userStorage.updateUser(user);
        return user;
    }

    @Override
    public User removeUser(Long id) {
        validateUserId(id);
        User user = userStorage.getUserById(id);
        userStorage.deleteUser(id);
        return user;
    }

    @Override
    public Collection<User> getFriends(long userId) {
        validateUserId(userId);
        return userStorage.getFriends(userId);
    }

    @Override
    public User addFriend(long userId, long friendId) {
        validateUserId(userId);
        validateUserId(friendId);
        User user = userStorage.addFriend(userId, friendId);
        feedStorage.addUserFeed(new UserFeed(0L,
                userId, friendId, Instant.now(),
                EventType.FRIEND, Operation.ADD));
        return user;
    }

    @Override
    public User deleteFriend(long userId, long friendId) {
        validateUserId(userId);
        validateUserId(friendId);
        User user = userStorage.deleteFriend(userId, friendId);
        feedStorage.addUserFeed(new UserFeed(0L,
                userId, friendId, Instant.now(),
                EventType.FRIEND, Operation.REMOVE
        ));
        return user;
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long friendId) {
        validateUserId(userId);
        validateUserId(friendId);
        return userStorage.getCommonFriends(userId, friendId);
    }

    @Override
    public User getUserById(long id) {
        validateUserId(id);
        return userStorage.getUserById(id);
    }

    @Override
    public Collection<UserFeed> getUserFeed(Long userId) {
        validateUserId(userId);
        return feedStorage.getUserFeed(userId);
    }

    @Override
    public void validateUserId(Long id) {
        try {
            userStorage.getUserById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Пользователь с ID: " + id + " не найден!");
        }
    }

    private void checkUserCriteria(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Введено пустое имя, поэтому имя изменено на логин: {}", user.getLogin());
        }
    }
}
