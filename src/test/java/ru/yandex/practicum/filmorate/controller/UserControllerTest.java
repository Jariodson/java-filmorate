package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {
    @Autowired
    private static ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserController controller;

    @BeforeAll
    static void beforeAll() {
        objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
    }

    @Test
    void testGetUsersShouldReturnUsers() throws Exception {
        User user1 = User.builder()
                .id(1)
                .name("Alex")
                .email("spring.a@yandex.ru")
                .login("alexSpring")
                .birthday(LocalDate.parse("1998-03-25"))
                .build();
        User user2 = User.builder()
                .id(1)
                .name("Joseph")
                .email("winter.j@yandex.ru")
                .login("josephWinter")
                .birthday(LocalDate.parse("1989-03-25"))
                .build();
        List<User> users = Arrays.asList(user1, user2);

        String gsonString = objectMapper.writeValueAsString(users);

        when(controller.getUsers()).thenReturn(users);
        this.mockMvc.perform(
                        get("/users"))
                .andExpect(status().isOk())
                .andExpect(content().json(gsonString))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void testAddUserShouldReturnNewUser() throws Exception {
        User user1 = User.builder()
                .id(1)
                .name("Alex")
                .email("spring.a@yandex.ru")
                .login("alexSpring")
                .birthday(LocalDate.parse("1998-03-25"))
                .build();
        when(controller.addUser(user1)).thenReturn(ResponseEntity.ok(user1));
        this.mockMvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(objectMapper.writeValueAsString(user1)));
    }

    @Test
    void testUpdateUserShouldReturnUserWithStatusNotFound() throws Exception {
        User user1 = User.builder()
                .id(1)
                .name("Alex")
                .email("spring.a@yandex.ru")
                .login("alexSpring")
                .birthday(LocalDate.parse("1998-03-25"))
                .build();
        when(controller.addUser(user1)).thenReturn(ResponseEntity.ok(user1));
        this.mockMvc.perform(put("/films")
                        .content(objectMapper.writeValueAsString(user1))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}