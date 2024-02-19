package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public Collection<User> getUsers() {
        return userStorage.getAllUsers();
    }

    public User createUser(User user) {
        userStorage.addNewUser(user);
        return user;
    }

    public User updateUser(User user) {
        userStorage.updateUser(user);
        return user;
    }

    public User removeUser(User user) {
        userStorage.deleteUser(user);
        return user;
    }

    public Collection<User> getFriends(long userId) {
        User user = userStorage.getUserById(userId);
        return user.getFriendsIds().stream()
                .map(userStorage::getUserById)
                .collect(Collectors.toList());
    }

    public User addFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.createFriend(friendId);
        friend.createFriend(userId);
        return user;
    }

    public User deleteFriend(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        user.removeFriend(friendId);
        friend.removeFriend(userId);
        return user;
    }

    public Collection<User> getCommonFriends(long userId, long friendId) {
        User user = userStorage.getUserById(userId);
        User friend = userStorage.getUserById(friendId);
        List<User> commonFriends = new ArrayList<>();
        user.getFriendsIds().stream()
                .filter(o -> friend.getFriendsIds().contains(o))
                .map(o -> userStorage.getUserById(o))
                .forEach(commonFriends::add);
        return commonFriends;
    }

    public User findUserById(long id) {
        return userStorage.getUserById(id);
    }
}
