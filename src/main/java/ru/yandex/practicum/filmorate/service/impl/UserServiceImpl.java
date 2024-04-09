package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.dal.UserStorage;

import java.util.Collection;

@Slf4j
@Service
@Transactional
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserServiceImpl(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
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
        validate(user.getId());
        userStorage.updateUser(user);
        return user;
    }

    @Override
    public User removeUser(Long id) {
        validate(id);
        User user = userStorage.getUserById(id);
        userStorage.deleteUser(id);
        return user;
    }

    @Override
    public Collection<User> getFriends(long userId) {
        validate(userId);
        return userStorage.getFriends(userId);
    }

    @Override
    public User addFriend(long userId, long friendId) {
        validate(userId);
        validate(friendId);
        return userStorage.addFriend(userId, friendId);
    }

    @Override
    public User deleteFriend(long userId, long friendId) {
        validate(userId);
        validate(friendId);
        return userStorage.deleteFriend(userId, friendId);
    }

    @Override
    public Collection<User> getCommonFriends(long userId, long friendId) {
        validate(userId);
        validate(friendId);
        return userStorage.getCommonFriends(userId, friendId);
    }

    @Override
    public User getUserById(long id) {
        validate(id);
        return userStorage.getUserById(id);
    }

    private void checkUserCriteria(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Введено пустое имя, поэтому имя изменено на логин: {}", user.getLogin());
        }
    }

    public void validate(Long id) {
        try {
            userStorage.getUserById(id);
        } catch (EmptyResultDataAccessException e) {
            throw new IllegalArgumentException("Пользователь с ID: " + id + " не найден!");
        }
    }
}
