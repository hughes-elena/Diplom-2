package ru.praktikums.tests;

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

import java.util.List;

@DisplayName("Тесты создания заказа")
public class OrderCreateTest {

    // Инициализируем шаги для работы с API пользователя и заказа
    private final UserSteps userSteps = new UserSteps();
    private final OrderSteps orderSteps = new OrderSteps();
    private final Faker faker = new Faker();

    // Создаем случайные тестовые данные пользователя
    private String email = faker.internet().emailAddress();
    private String password = faker.internet().password();
    private String name = faker.name().username();


    private final UserCreateAndEditRequest userData = new UserCreateAndEditRequest(email, password, name);
    private final UserLoginRequest loginRequest = new UserLoginRequest(email, password);

    private String accessToken;

    private final List<String> validIngredients = List.of("61c0c5a71d1f82001bdaaa6d");

    @Before
    public void setUp() {
        userSteps.createUser(userData)
                .statusCode(SC_OK);
        accessToken = userSteps.getAccessToken(loginRequest);
    }

    @After
    public void tearDown() {
        if (accessToken != null) {
            userSteps.userDelete(accessToken)
                    .statusCode(SC_ACCEPTED);
        }
    }

    @Test
    @DisplayName("Создание заказа после авторизации")
    @Description("Проверка возможности создания заказа после авторизации")
    public void createOrderWithAuthorization() {
        OrderCreateRequest order = new OrderCreateRequest(validIngredients);
        orderSteps.orderCreateAfterLogin(loginRequest, order)
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа без авторизации")
    @Description("Проверка возможности создания заказа без авторизации")
    public void createOrderWithOutAuthorization() {
        OrderCreateRequest order = new OrderCreateRequest(validIngredients);
        orderSteps.orderCreate(order)
                .statusCode(SC_OK)
                .body("success", equalTo(true));
    }

    @Test
    @DisplayName("Создание заказа после авторизации без ингредиентов")
    @Description("Проверка невозможности создания заказа после авторизации без ингредиентов")
    public void createOrderWithAuthWithOutIngredients() {
        OrderCreateRequest emptyOrder = new OrderCreateRequest(List.of());

        orderSteps.orderCreateAfterLogin(loginRequest, emptyOrder)
                .statusCode(SC_BAD_REQUEST)
                .body("success", equalTo(false));
    }

    @Test
    @DisplayName("Создание заказа после авторизации с неверным ингредиентом")
    @Description("Проверка невозможности создания заказа после авторизации c неверным ингредиентом")
    public void orderCreateWithAuthorizationWithWrongIngredients() {
        // Передаём невалидный ID ингредиента
        List<String> invalidIngredients = List.of("123456invalid");

        OrderCreateRequest order = new OrderCreateRequest(invalidIngredients);

        orderSteps.orderCreateAfterLogin(loginRequest, order)
                .statusCode(SC_INTERNAL_SERVER_ERROR);
    }

}