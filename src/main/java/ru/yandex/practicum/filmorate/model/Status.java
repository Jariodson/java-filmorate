package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.Enums.FriendshipStatus;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Status {
    private Long id;
    private FriendshipStatus status;
}
