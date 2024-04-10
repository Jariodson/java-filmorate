package ru.yandex.practicum.filmorate.storage.dal;

import ru.yandex.practicum.filmorate.model.UserFeed;

import java.util.Collection;

public interface UserFeedDal {

    void addUserFeed(UserFeed feed);

    Collection<UserFeed> getUserFeed(Long userId);
}
