package ru.praktikums;

import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import org.junit.After;
import org.junit.Test;
import ru.praktikums.models.pojo.*;
import ru.praktikums.steps.*;

import static org.apache.http.HttpStatus.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;

import com.github.javafaker.Faker; // для генерации случайных данных

@DisplayName("Тесты API создания пользователя")
public class UserCreateTest {

    // Инициализируем шаги для работы с API пользователя
    private final UserSteps userSteps = new UserSteps();
    private final Faker faker = new Faker(); // Создаём объект Faker

    // Создаем случайные тестовые данные пользователя
    private String email = faker.internet().emailAddress();
    private String password = faker.internet().password();
    private String name = faker.name().username();

    private final UserCreateAndEditRequest loginData = new UserCreateAndEditRequest(email, password, name);
    private final UserLoginRequest loginRequest = new UserLoginRequest(email, password);

    @Test
    @DisplayName("Создание уникального пользователя")
    @Description("Проверка, что можно создать нового уникального пользователя")
    public void testCreateUser() {
        userSteps.createUser(loginData)
                .assertThat()
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
    @DisplayName("Создание пользователя, который уже зарегистрирован")
    @Description("Проверка, что нельзя создать пользователя, который уже зарегистрирован")
    public void testSameUserCreation() {
        // Первый раз — успешно
        userSteps.createUser(loginData)
                .assertThat()
                .statusCode(SC_OK)
                .body("success", equalTo(true));

        // Второй раз — ошибка 403
        userSteps.createUser(loginData)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("User already exists"));
        userSteps.userDeleteAfterLogin(loginRequest);
    }

    @Test
    @DisplayName("Создание пользователя без поля email")
    @Description("Проверка, что нельзя создать пользователя без email")
    public void testCreateUserWithoutEmail() {
        UserCreateAndEditRequest userWithoutEmail = new UserCreateAndEditRequest(null, password, name);

        userSteps.createUser(userWithoutEmail)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без поля password")
    @Description("Проверка, что нельзя создать пользователя без password")
    public void testCreateUserWithoutPassword() {
        UserCreateAndEditRequest userWithoutEmail = new UserCreateAndEditRequest(email, null, name);

        userSteps.createUser(userWithoutEmail)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без поля name")
    @Description("Проверка, что нельзя создать пользователя без name")
    public void testCreateUserWithoutName() {
        UserCreateAndEditRequest userWithoutEmail = new UserCreateAndEditRequest(email, password, null);

        userSteps.createUser(userWithoutEmail)
                .assertThat()
                .statusCode(SC_FORBIDDEN)
                .body("message", equalTo("Email, password and name are required fields"));
    }

}