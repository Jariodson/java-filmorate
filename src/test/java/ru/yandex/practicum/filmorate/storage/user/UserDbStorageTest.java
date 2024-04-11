package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.dal.UserStorage;
import ru.yandex.practicum.filmorate.storage.dal.dao.UserDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.annotation.DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD;

@JdbcTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = BEFORE_EACH_TEST_METHOD)
public class UserDbStorageTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private UserStorage userDbStorage;

    @BeforeEach
    void beforeEach() {
        userDbStorage = new UserDbStorage(jdbcTemplate);
    }

    @Test
    public void testFindUserById() {
        // Подготавливаем данные для теста
        User newUser = User.builder()
                .id(1L)
                .email("user@email.ru")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .login("vanya123")
                .build();
        userDbStorage.addNewUser(newUser);

        // вызываем тестируемый метод
        User savedUser = userDbStorage.getUserById(1L);

        // проверяем утверждения
        assertThat(savedUser)
                .isNotNull() // проверяем, что объект не равен null
                .usingRecursiveComparison(); // проверяем, что значения полей нового
    }

    @Test
    public void testGetAllUsersShouldReturnAllUsersFromDb() {
        User user1 = User.builder()
                .email("user@email.ru")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .login("vanya123")
                .build();
        User user2 = User.builder()
                .email("user2@email.ru")
                .name("Gen Gacha")
                .birthday(LocalDate.of(1995, 10, 23))
                .login("genka123")
                .build();
        userDbStorage.addNewUser(user1);
        userDbStorage.addNewUser(user2);

        Collection<User> users = userDbStorage.getAllUsers();
        assertThat(users).isNotNull();
    }

    @Test
    public void testAddNewUserShouldAddUserToDb() {
        User user1 = User.builder()
                .email("user@email.ru")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .login("vanya123")
                .build();
        userDbStorage.addNewUser(user1);
        User newUser = userDbStorage.getUserById(1L);
        assertThat(newUser).isNotNull();
    }

    @Test
    public void testUpdateUserShouldAddToDbNewInformationAboutUser() {
        User user1 = User.builder()
                .email("user@email.ru")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .login("vanya123")
                .build();
        userDbStorage.addNewUser(user1);
        User user2 = User.builder()
                .id(1L)
                .email("user2@email.ru")
                .name("Georg Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .login("vanya123")
                .build();

        userDbStorage.updateUser(user2);
        assertThat(userDbStorage.getUserById(1L)).isNotNull();
    }

    @Test
    public void testShouldDeleteUserFromDb() {
        User user1 = User.builder()
                .email("user@email.ru")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .login("vanya123")
                .build();
        userDbStorage.addNewUser(user1);
        assertThat(userDbStorage.getUserById(1L)).isNotNull();
        userDbStorage.deleteUser(user1.getId());
        assertThat(userDbStorage.getAllUsers()).isEqualTo(List.of());
    }

    @Test
    void getFriends() {
        User user1 = User.builder()
                .email("user@email.ru")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .login("vanya123")
                .build();
        userDbStorage.addNewUser(user1);
        User user2 = User.builder()
                .email("user2@email.ru")
                .name("Georg Petrov")
                .birthday(LocalDate.of(1995, 3, 18))
                .login("georg123")
                .build();
        userDbStorage.addNewUser(user2);
        User user = userDbStorage.addFriend(user1.getId(), user2.getId());

        assertThat(List.of(userDbStorage.getFriends(user.getId()))).isNotNull();

        System.out.println();
        System.out.println(userDbStorage.getFriends(2L));
        System.out.println();
    }

    @Test
    void addFriend() {
        User user1 = User.builder()
                .email("user@email.ru")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .login("vanya123")
                .build();
        userDbStorage.addNewUser(user1);
        User user2 = User.builder()
                .email("user2@email.ru")
                .name("Georg Petrov")
                .birthday(LocalDate.of(1995, 3, 18))
                .login("georg123")
                .build();
        userDbStorage.addNewUser(user2);
        User user = userDbStorage.addFriend(user1.getId(), user2.getId());
        assertThat(user).isNotNull();
    }

    @Test
    void deleteFriend() {
        User user1 = User.builder()
                .email("user@email.ru")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .login("vanya123")
                .build();
        userDbStorage.addNewUser(user1);
        User user2 = User.builder()
                .email("user2@email.ru")
                .name("Georg Petrov")
                .birthday(LocalDate.of(1995, 3, 18))
                .login("georg123")
                .build();
        userDbStorage.addNewUser(user2);
        userDbStorage.addFriend(user1.getId(), user2.getId());

        User user = userDbStorage.deleteFriend(user1.getId(), user2.getId());
        assertThat(user).isNotNull();
    }

    @Test
    void getCommonFriends() {
        User user1 = User.builder()
                .email("user@email.ru")
                .name("Ivan Petrov")
                .birthday(LocalDate.of(1990, 1, 1))
                .login("vanya123")
                .build();
        userDbStorage.addNewUser(user1);
        User user2 = User.builder()
                .email("user2@email.ru")
                .name("Georg Petrov")
                .birthday(LocalDate.of(1995, 3, 18))
                .login("georg123")
                .build();
        userDbStorage.addNewUser(user2);
        User user3 = User.builder()
                .email("p.durov@email.ru")
                .name("Pavel Durov")
                .birthday(LocalDate.of(1987, 7, 4))
                .login("durov123")
                .build();
        userDbStorage.addNewUser(user3);
        userDbStorage.addFriend(user1.getId(), user2.getId());
        userDbStorage.addFriend(user1.getId(), user3.getId());
        userDbStorage.addFriend(user2.getId(), user3.getId());

        Collection<User> users = userDbStorage.getCommonFriends(user1.getId(), user2.getId());
        assertThat(users).isNotNull();
    }
}