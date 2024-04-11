package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.InstantSerializer;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class UserFeed {
    Long eventId;
    Long userId;
    Long entityId;
    @JsonSerialize(using = InstantSerializer.class)
    Instant timestamp;
    EventType eventType;
    Operation operation;

}
