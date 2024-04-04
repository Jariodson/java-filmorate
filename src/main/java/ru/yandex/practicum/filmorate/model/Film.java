package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма не может быть отрицательной или равна 0!")
    private final int duration;

    private Set<Long> likes = new HashSet<>();
    private Long id;
    private Mpa mpa;
    private Set<Genre> genres = new HashSet<>();
    private Set<Director> directors = new HashSet<>();
}
