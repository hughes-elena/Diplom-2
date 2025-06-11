package ru.praktikums.steps;

import io.qameta.allure.Step;

import io.restassured.response.ValidatableResponse;
import ru.praktikums.constants.ApiEndPoints;
import ru.praktikums.models.pojo.OrderCreateRequest;
import ru.praktikums.models.pojo.UserLoginRequest;
import static ru.praktikums.steps.BaseSteps.requestSpecification;



public class OrderSteps {

    @Step("Создание нового заказа без авторизации")
    public ValidatableResponse orderCreate(OrderCreateRequest orderCreateRequest) {
        return requestSpecification()
                .body(orderCreateRequest)
                .post(ApiEndPoints.ORDER_CREATE_POST)
                .then();
    }

    @Step("Создание нового заказа после авторизации")
    public ValidatableResponse orderCreateAfterLogin(UserLoginRequest userLoginRequest, OrderCreateRequest orderCreateRequest) {
        UserSteps userSteps = new UserSteps();
        String accessToken = userSteps.getAccessToken(userLoginRequest);

        return requestSpecification()
                .header("Authorization", accessToken)
                .body(orderCreateRequest)
                .post(ApiEndPoints.ORDER_CREATE_POST)
                .then();
    }

    @Step("Получение заказов без авторизации")
    public ValidatableResponse orderList() {
        return requestSpecification()
                .get(ApiEndPoints.ORDER_CREATE_POST)
                .then();
    }
    @Step("Получение заказов после авторизации")
    public ValidatableResponse orderListAfterLogin(UserLoginRequest userLoginRequest) {
        UserSteps userSteps = new UserSteps();
        String accessToken = userSteps.getAccessToken(userLoginRequest);

        return requestSpecification()
                .header("Authorization", accessToken)
                .get(ApiEndPoints.ORDER_CREATE_POST)
                .then();
    }

}
