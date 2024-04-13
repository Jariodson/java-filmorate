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
    private Long eventId;
    private Long userId;
    private Long entityId;
    @JsonSerialize(using = InstantSerializer.class)
    private Instant timestamp;
    private EventType eventType;
    private Operation operation;

}
