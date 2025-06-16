package ru.praktikums;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.praktikums.models.pojo.*;
import ru.praktikums.steps.*;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.CoreMatchers.equalTo;

import com.github.javafaker.Faker; // для генерации случайных данных

@DisplayName("Тесты API для логина пользователя")
public class UserLoginTest {

    // Инициализируем шаги для работы с API пользователя
    private final UserSteps userSteps = new UserSteps();
    private final Faker faker = new Faker(); // Создаём объект Faker

    // Создаем случайные тестовые данные пользователя
    private String email = faker.internet().emailAddress();
    private String password = faker.internet().password();
    private String name = faker.name().username();

    private final UserCreateAndEditRequest userData = new UserCreateAndEditRequest(email, password, name);
    private final UserLoginRequest loginRequest = new UserLoginRequest(email, password);

    @Before
    public void setUp() {
        // Создаём пользователя перед тестом логина
        userSteps.createUser(userData)
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @After
    public void tearDown() {
        try {
            String accessToken = userSteps.getAccessToken(loginRequest);
            if (accessToken != null) {
                userSteps.userDelete(accessToken)
                        .statusCode(SC_ACCEPTED);
            }
        } catch (Exception e) {
            System.out.println("Пользователь не был создан, удаление не требуется");
        }
    }

    @Test
    @DisplayName("Логин под существующим пользователем")
    @Description("Проверка возможности логина под существующим пользователем")
    public void userLogin() {
        userSteps.loginUser(loginRequest)
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Логин с неверным email")
    @Description("Проверка не возможности логина с неверным email")
    public void userLoginWithWrongEmail() {
        UserLoginRequest wrongEmailRequest = new UserLoginRequest("wrong_email@ya.com", password);
        userSteps.loginUser(wrongEmailRequest)
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }

    @Test
    @DisplayName("Логин с неверным password")
    @Description("Проверка не возможности логина с неверным password")
    public void userLoginWithWrongPassword() {
        UserLoginRequest wrongPasswordRequest = new UserLoginRequest(email, "wrongPassword");
        userSteps.loginUser(wrongPasswordRequest)
                .statusCode(SC_UNAUTHORIZED)
                .body("success", equalTo(false))
                .body("message", equalTo("email or password are incorrect"));
    }
}