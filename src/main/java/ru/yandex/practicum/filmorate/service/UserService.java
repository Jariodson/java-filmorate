package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserService {
    Collection<User> getUsers();

    User createUser(User user);

    User updateUser(User user);

    User removeUser(User user);

    Collection<Long> getFriends(long userId);

    User addFriend(long userId, long friendId);

    User deleteFriend(long userId, long friendId);

    Collection<User> getCommonFriends(long userId, long friendId);

    User findUserById(long id);
}
