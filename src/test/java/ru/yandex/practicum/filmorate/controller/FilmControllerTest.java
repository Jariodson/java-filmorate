package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class FilmControllerTest {
    @Autowired
    private static ObjectMapper objectMapper;
    @InjectMocks
    FilmController controller;
    private MockMvc mockMvc;
    @Autowired
    private FilmStorage filmStorage;
    @Mock
    private FilmService filmService;

    @BeforeEach
    void beforeEach() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testAddFilmShouldReturnFilms() throws Exception {
        Film film1 = Film.builder()
                .id(1L)
                .name("Aladdin")
                .description("Cartoon about prince")
                .releaseDate(LocalDate.parse("1967-03-25"))
                .duration(90)
                .build();
        Film film2 = Film.builder()
                .id(2L)
                .name("Rusalochka")
                .description("Cartoon about the see princes")
                .releaseDate(LocalDate.parse("1900-03-25"))
                .duration(75)
                .build();
        List<Film> films = Arrays.asList(film1, film2);

        String gsonString = objectMapper.writeValueAsString(films);
        when(filmService.getFilms()).thenReturn(films);
        this.mockMvc.perform(
                        get("/films"))
                .andExpect(status().isOk())
                .andExpect(content().json(gsonString))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testAddFilmShouldAddNewFilm() throws Exception {
        Film film1 = Film.builder()
                .name("Aladdin")
                .description("Cartoon about prince")
                .releaseDate(LocalDate.parse("1967-03-25"))
                .duration(90)
                .build();
        when(filmService.addFilm(film1)).thenReturn(ResponseEntity.ok(film1));
        this.mockMvc.perform(post("/films")
                        .content(objectMapper.writeValueAsString(film1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void testUpdateFilmShouldUpdateReleasedDateAndReturnStatusOk() throws Exception {
        Film film1 = Film.builder()
                .id(1L)
                .name("Aladdin")
                .description("Cartoon about prince")
                .releaseDate(LocalDate.parse("1967-03-25"))
                .duration(90)
                .build();
        when(filmService.updateFilm(film1)).thenReturn(ResponseEntity.ok(film1));
        this.mockMvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(film1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", notNullValue()))
                .andDo(print());
    }

    @Test
    void testShouldAddLikeToFilm() throws Exception {
        Film film1 = Film.builder()
                .id(1L)
                .name("Aladdin")
                .description("Cartoon about prince")
                .releaseDate(LocalDate.parse("1967-03-25"))
                .duration(90)
                .build();
        User user1 = User.builder()
                .id(1L)
                .name("Alex")
                .email("spring.a@yandex.ru")
                .login("alexSpring")
                .birthday(LocalDate.parse("1998-03-25"))
                .build();
        Film film2 = film1.toBuilder().build();
        film2.addLike(user1.getId());
        when(filmService.addLike(1L, 1L)).thenReturn(ResponseEntity.ok(film2));
        this.mockMvc.perform(put("/films/1/like/1")
                        .content(objectMapper.writeValueAsString(film1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void testShouldDeleteLike() throws Exception {
        Film film1 = Film.builder()
                .id(1L)
                .name("Aladdin")
                .description("Cartoon about prince")
                .releaseDate(LocalDate.parse("1967-03-25"))
                .duration(90)
                .build();
        User user1 = User.builder()
                .id(1L)
                .name("Alex")
                .email("spring.a@yandex.ru")
                .login("alexSpring")
                .birthday(LocalDate.parse("1998-03-25"))
                .build();
        film1.addLike(user1.getId());
        Film film = film1.toBuilder().build();
        film.removeLike(user1.getId());
        when(controller.removeLike(Optional.of(1L), Optional.of(1L))).thenReturn(ResponseEntity.ok(film));
        this.mockMvc.perform(delete("/films/1/like/1")
                        .content(objectMapper.writeValueAsString(film1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    void testGetFavouriteFilmsShouldReturnMostFavouriteFilms() throws Exception {
        Film film1 = Film.builder()
                .id(1L)
                .name("Aladdin")
                .description("Cartoon about prince")
                .releaseDate(LocalDate.parse("1967-03-25"))
                .duration(90)
                .build();
        Film film2 = Film.builder()
                .id(2L)
                .name("Rusalochka")
                .description("Cartoon about the see princes")
                .releaseDate(LocalDate.parse("1900-03-25"))
                .duration(75)
                .build();
        Film film3 = Film.builder()
                .name("Film3")
                .description("film3 description")
                .releaseDate(LocalDate.parse("2003-03-23"))
                .duration(123)
                .build();
        User user1 = User.builder()
                .id(1L)
                .name("Alex")
                .email("spring.a@yandex.ru")
                .login("alexSpring")
                .birthday(LocalDate.parse("1998-03-25"))
                .build();
        User user2 = User.builder()
                .name("Russ")
                .email("russ.spring@yandex.ru")
                .login("russSpring")
                .birthday(LocalDate.parse("2003-03-02"))
                .build();
        film1.addLike(user1.getId());
        film2.addLike(user2.getId());
        film2.addLike(user1.getId());
        when(controller.getFavouriteFilms(2)).thenReturn(List.of(film1, film2));
        this.mockMvc.perform(get("/films/popular?count=2"))
                .andExpect(status().isOk())
                .andDo(print());
    }
}