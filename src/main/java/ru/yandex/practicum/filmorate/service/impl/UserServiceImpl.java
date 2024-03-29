package ru.yandex.practicum.filmorate.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.dal.UserStorage;

import java.util.Collection;
import java.util.List;

@Service
@Slf4j
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
    @Transactional
    public User createUser(User user) {
        userStorage.addNewUser(user);
        return user;
    }

    @Override
    @Transactional
    public User updateUser(User user) {
        userStorage.updateUser(user);
        return user;
    }

    @Override
    @Transactional
    public User removeUser(User user) {
        userStorage.deleteUser(user);
        return user;
    }

    @Override
    @Transactional
    public Collection<User> getFriends(long userId) {
        return userStorage.getFriends(userId);
    }

    @Override
    @Transactional
    public User addFriend(long userId, long friendId) {
        return userStorage.addFriend(userId, friendId);
    }

    @Override
    @Transactional
    public User deleteFriend(long userId, long friendId) {
        return userStorage.deleteFriend(userId, friendId);
    }

    @Override
    @Transactional
    public Collection<User> getCommonFriends(long userId, long friendId) {
        return userStorage.getCommonFriends(userId, friendId);
    }

    @Override
    @Transactional
    public User findUserById(long id) {
        return userStorage.getUserById(id);
    }
}
