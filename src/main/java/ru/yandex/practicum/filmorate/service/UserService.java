package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.UserFeed;

import java.util.Collection;

public interface UserService {
    Collection<User> getUsers();

    User createUser(User user);

    User updateUser(User user);

    User removeUser(Long id);

    Collection<User> getFriends(long userId);

    User addFriend(long userId, long friendId);

    User deleteFriend(long userId, long friendId);

    Collection<User> getCommonFriends(long userId, long friendId);

    User getUserById(long id);

    void validateUserId(Long userId);

    Collection<UserFeed> getUserFeed(Long userId);
}
