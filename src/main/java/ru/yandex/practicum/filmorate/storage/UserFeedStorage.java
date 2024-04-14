package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.UserFeed;

import java.util.Collection;

public interface UserFeedStorage {

    void addUserFeed(UserFeed feed);

    Collection<UserFeed> getUserFeed(Long userId);
}
