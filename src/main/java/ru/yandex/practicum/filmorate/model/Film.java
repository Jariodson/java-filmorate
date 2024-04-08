package ru.yandex.practicum.filmorate.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.filmorate.validation.DateMin;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Film.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class Film {
    @NotBlank(message = "Название не может быть пустым")
    private final String name;
    @Size(max = 200, message = "Длина описания превышает лимит! Лимит 200!")
    private final String description;
    @DateMin(value = "1895-12-28")
    private final LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма не может быть отрицательной или равна 0!")
    private final int duration;
    @Builder.Default
    private Set<Long> likes = new HashSet<>();
    private Long id;
    private Mpa mpa;
    @Builder.Default
    private Collection<Genre> genres = new ArrayList<>();
    @Builder.Default
    private Collection<Director> directors = new ArrayList<>();
}
