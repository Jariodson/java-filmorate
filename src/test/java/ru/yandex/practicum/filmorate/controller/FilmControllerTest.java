package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FilmController.class)
class FilmControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private static ObjectMapper objectMapper;
    @MockBean
    private FilmController controller;

    @BeforeAll
    static void beforeAll() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
    }

    @Test
    void testAddFilmShouldReturnFilms() throws Exception {
        Film film1 = Film.builder()
                .id(1)
                .name("Aladdin")
                .description("Cartoon about prince")
                .releaseDate(LocalDate.parse("1967-03-25"))
                .duration(90)
                .build();
        Film film2 = Film.builder()
                .id(2)
                .name("Rusalochka")
                .description("Cartoon about the see princes")
                .releaseDate(LocalDate.parse("1900-03-25"))
                .duration(75)
                .build();
        List<Film> films = Arrays.asList(film1, film2);

        String gsonString = objectMapper.writeValueAsString(films);

        when(controller.getFilms()).thenReturn(films);
        this.mockMvc.perform(
                        get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json(gsonString))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testAddFilmShouldAddNewFilm() throws Exception {
        Film film1 = Film.builder()
                .id(1)
                .name("Aladdin")
                .description("Cartoon about prince")
                .releaseDate(LocalDate.parse("1967-03-25"))
                .duration(90)
                .build();
        when(controller.addFilm(film1)).thenReturn(ResponseEntity.ok(film1));
        this.mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(film1)));
    }

    @Test
    void testUpdateFilmShouldReturnStatusNotFound() throws Exception {
        Film film1 = Film.builder()
                .id(1)
                .name("Aladdin")
                .description("Cartoon about prince")
                .releaseDate(LocalDate.parse("1967-03-25"))
                .duration(90)
                .build();
        when(controller.addFilm(film1)).thenReturn(ResponseEntity.ok(film1));
        this.mockMvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}